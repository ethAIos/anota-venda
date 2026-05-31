package com.caderninho.vendas.data.repo

import androidx.room.withTransaction
import com.caderninho.vendas.data.db.CaderninhoDatabase
import com.caderninho.vendas.data.db.entities.CustomerEntity
import com.caderninho.vendas.data.db.entities.InstallmentEntity
import com.caderninho.vendas.data.db.entities.OrderEntity
import com.caderninho.vendas.data.model.OrderStatus
import com.caderninho.vendas.data.model.PaymentMode
import com.caderninho.vendas.ui.components.AvatarTone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class PayingTodayRow(
    val installment: InstallmentEntity,
    val order: OrderEntity,
    val customer: CustomerEntity,
)

data class OpenInstallmentRow(
    val installment: InstallmentEntity,
    val order: OrderEntity,
    val customer: CustomerEntity,
    val status: OrderStatus,
)

data class CustomerOrderSummary(
    val order: OrderEntity,
    val installments: List<InstallmentEntity>,
    val status: OrderStatus,
    val openBalanceCents: Long,
)

data class CustomerSummary(
    val customer: CustomerEntity,
    val orders: List<CustomerOrderSummary>,
    val openBalanceCents: Long,
    val openOrderCount: Int,
    val overdueOrderCount: Int,
)

data class OrderDetail(
    val order: OrderEntity,
    val customer: CustomerEntity,
    val installments: List<InstallmentEntity>,
)

@Singleton
class SalesRepository @Inject constructor(
    private val db: CaderninhoDatabase,
) {
    private val customerDao = db.customerDao()
    private val orderDao = db.orderDao()
    private val installmentDao = db.installmentDao()

    fun observeAllCustomers() = customerDao.observeAll()
    fun observeOpenInstallments() = installmentDao.observeOpen()

    fun observeCustomer(id: Long) = customerDao.observeById(id)
    fun observeOrdersByCustomer(id: Long) = orderDao.observeByCustomer(id)

    suspend fun getCustomer(id: Long): CustomerEntity? = customerDao.getById(id)

    /** Unpaid installment with order/customer for receive sheet (any due date). */
    suspend fun getPayingTodayRow(installmentId: Long): PayingTodayRow? {
        val installment = installmentDao.getById(installmentId) ?: return null
        if (installment.paidAt != null) return null
        val order = orderDao.getById(installment.orderId) ?: return null
        val customer = customerDao.getById(order.customerId) ?: return null
        return PayingTodayRow(installment, order, customer)
    }

    fun observePayingToday(today: LocalDate = LocalDate.now()): Flow<List<PayingTodayRow>> =
        combine(
            installmentDao.observeDueOn(today),
            customerDao.observeAll(),
            // we need order info; fetch a flow of all orders by combining with installments map
            installmentDao.observeOpen(),
        ) { dueToday, customers, _ ->
            val customerById = customers.associateBy { it.id }
            dueToday.mapNotNull { inst ->
                val order = orderDao.getById(inst.orderId) ?: return@mapNotNull null
                val customer = customerById[order.customerId] ?: return@mapNotNull null
                PayingTodayRow(inst, order, customer)
            }
        }

    fun observeOpenDueBetween(startDate: LocalDate, endDate: LocalDate): Flow<List<PayingTodayRow>> =
        combine(
            installmentDao.observeOpenDueBetween(startDate, endDate),
            customerDao.observeAll(),
        ) { installments, customers ->
            val customerById = customers.associateBy { it.id }
            installments.mapNotNull { inst ->
                val order = orderDao.getById(inst.orderId) ?: return@mapNotNull null
                val customer = customerById[order.customerId] ?: return@mapNotNull null
                PayingTodayRow(inst, order, customer)
            }
        }

    fun observeOpenWithDetail(today: LocalDate = LocalDate.now()): Flow<List<OpenInstallmentRow>> =
        combine(
            installmentDao.observeOpen(),
            customerDao.observeAll(),
        ) { installments, customers ->
            val customerById = customers.associateBy { it.id }
            installments.mapNotNull { inst ->
                val order = orderDao.getById(inst.orderId) ?: return@mapNotNull null
                val customer = customerById[order.customerId] ?: return@mapNotNull null
                val status = when {
                    inst.dueDate.isBefore(today) -> OrderStatus.OVERDUE
                    inst.dueDate == today -> OrderStatus.DUE_TODAY
                    else -> OrderStatus.OPEN
                }
                OpenInstallmentRow(inst, order, customer, status)
            }
        }

    fun observeCustomerSummary(
        customerId: Long,
        today: LocalDate = LocalDate.now(),
    ): Flow<CustomerSummary?> = combine(
        customerDao.observeById(customerId),
        orderDao.observeByCustomer(customerId),
        installmentDao.observeByCustomer(customerId),
    ) { customer, orders, installments ->
        if (customer == null) return@combine null
        val instByOrder = installments.groupBy { it.orderId }
        val orderSummaries = orders.map { order ->
            val items = instByOrder[order.id].orEmpty()
            val open = items.filter { it.paidAt == null }
            val openSum = open.sumOf { it.amountCents }
            val status = when {
                open.isEmpty() -> OrderStatus.PAID
                open.any { it.dueDate.isBefore(today) } -> OrderStatus.OVERDUE
                open.any { it.dueDate == today } -> OrderStatus.DUE_TODAY
                else -> OrderStatus.OPEN
            }
            CustomerOrderSummary(order, items, status, openSum)
        }
        CustomerSummary(
            customer = customer,
            orders = orderSummaries,
            openBalanceCents = orderSummaries.sumOf { it.openBalanceCents },
            openOrderCount = orderSummaries.count { it.status != OrderStatus.PAID },
            overdueOrderCount = orderSummaries.count { it.status == OrderStatus.OVERDUE },
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeOrderDetail(orderId: Long): Flow<OrderDetail?> =
        orderDao.observeById(orderId).flatMapLatest { order ->
            if (order == null) return@flatMapLatest flowOf(null)
            installmentDao.observeByOrder(orderId).combine(
                customerDao.observeById(order.customerId),
            ) { installments, customer ->
                if (customer == null) null
                else OrderDetail(order, customer, installments)
            }
        }

    suspend fun recordSale(
        customerName: String,
        phone: String?,
        what: String,
        totalCents: Long,
        paymentMode: PaymentMode,
        firstDueDate: LocalDate,
        installmentCount: Int = 1,
        observation: String? = null,
        avatarTone: AvatarTone = AvatarTone.AMBER,
    ): Long = db.withTransaction {
        val existing = customerDao.findByName(customerName)
        val customerId = existing?.id ?: customerDao.upsert(
            CustomerEntity(name = customerName, phone = phone, avatarTone = avatarTone)
        )
        if (existing != null && phone != null && existing.phone == null) {
            customerDao.update(existing.copy(phone = phone))
        }
        val orderId = orderDao.insert(
            OrderEntity(
                customerId = customerId,
                what = what,
                createdAt = LocalDate.now(),
                totalCents = totalCents,
                paymentMode = paymentMode,
                observation = observation,
            )
        )
        val perInstallment = totalCents / installmentCount
        val remainder = totalCents - perInstallment * installmentCount
        val items = (1..installmentCount).map { n ->
            val cents = if (n == installmentCount) perInstallment + remainder else perInstallment
            val due = if (paymentMode == PaymentMode.AVISTA) LocalDate.now()
                      else firstDueDate.plusMonths((n - 1).toLong())
            InstallmentEntity(
                orderId = orderId,
                number = n,
                ofTotal = installmentCount,
                dueDate = due,
                amountCents = cents,
                paidAt = if (paymentMode == PaymentMode.AVISTA) LocalDate.now() else null,
            )
        }
        installmentDao.insertAll(items)
        orderId
    }

    suspend fun markPaid(installmentId: Long, date: LocalDate = LocalDate.now()) {
        installmentDao.markPaid(installmentId, date)
    }

    suspend fun postpone(installmentId: Long, newDate: LocalDate) {
        installmentDao.postpone(installmentId, newDate)
    }

    suspend fun updateOrderDetails(orderId: Long, what: String, observation: String?) {
        val current = orderDao.getById(orderId) ?: return
        orderDao.update(
            current.copy(
                what = what.trim(),
                observation = observation?.trim()?.takeIf { it.isNotBlank() },
            ),
        )
    }

    suspend fun unmarkPaid(installmentId: Long) {
        installmentDao.unmarkPaid(installmentId)
    }

    suspend fun updateInstallmentAmount(installmentId: Long, cents: Long) {
        if (cents <= 0L) throw IllegalStateException("valor precisa ser maior que zero")
        installmentDao.updateAmount(installmentId, cents)
    }

    suspend fun updateCustomer(
        id: Long,
        name: String,
        phone: String?,
        avatarTone: AvatarTone,
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) throw IllegalStateException("nome não pode ficar em branco")
        val current = customerDao.getById(id) ?: return
        customerDao.update(
            current.copy(
                name = trimmedName,
                phone = phone?.trim()?.takeIf { it.isNotBlank() },
                avatarTone = avatarTone,
            ),
        )
    }

    suspend fun deleteCustomer(id: Long) {
        customerDao.deleteById(id)
    }

    suspend fun deleteOrder(orderId: Long) {
        orderDao.deleteById(orderId)
    }

    suspend fun clearAllData() {
        // Cascade: deleting customers also deletes their orders and installments.
        customerDao.deleteAll()
    }

    suspend fun updateOrder(
        orderId: Long,
        what: String,
        observation: String?,
        createdAt: LocalDate,
        newTotalCents: Long,
    ) {
        if (newTotalCents <= 0L) throw IllegalStateException("total precisa ser maior que zero")
        val trimmedWhat = what.trim()
        if (trimmedWhat.isBlank()) throw IllegalStateException("descrição do pedido não pode ficar em branco")
        db.withTransaction {
            val current = orderDao.getById(orderId) ?: return@withTransaction
            val orderInstallments = installmentDao.getByOrder(orderId)
            val paidSum = orderInstallments.filter { it.paidAt != null }.sumOf { it.amountCents }
            if (newTotalCents < paidSum) {
                throw IllegalStateException("total abaixo do já recebido")
            }
            orderDao.update(
                current.copy(
                    what = trimmedWhat,
                    observation = observation?.trim()?.takeIf { it.isNotBlank() },
                    createdAt = createdAt,
                    totalCents = newTotalCents,
                ),
            )
            if (newTotalCents != current.totalCents) {
                val open = orderInstallments.filter { it.paidAt == null }.sortedBy { it.number }
                if (open.isEmpty()) {
                    throw IllegalStateException("todas as parcelas estão pagas; não dá pra alterar o total")
                }
                val remaining = newTotalCents - paidSum
                val per = remaining / open.size
                val remainder = remaining - per * open.size
                open.forEachIndexed { index, inst ->
                    val cents = if (index == open.lastIndex) per + remainder else per
                    installmentDao.updateAmount(inst.id, cents)
                }
            }
        }
    }

    suspend fun exportCsv(today: LocalDate = LocalDate.now()): String {
        val customersById = customerDao.getAll().associateBy { it.id }
        val ordersById = orderDao.getAll().associateBy { it.id }
        val header = listOf(
            "cliente",
            "telefone",
            "produto",
            "data_pedido",
            "parcela",
            "vencimento",
            "valor",
            "pago_em",
            "status",
            "observacao",
        )
        val lines = installmentDao.getAll().mapNotNull { installment ->
            val order = ordersById[installment.orderId] ?: return@mapNotNull null
            val customer = customersById[order.customerId] ?: return@mapNotNull null
            val status = when {
                installment.paidAt != null -> "pago"
                installment.dueDate.isBefore(today) -> "atrasado"
                installment.dueDate == today -> "vence_hoje"
                else -> "em_aberto"
            }
            listOf(
                customer.name,
                customer.phone.orEmpty(),
                order.what,
                order.createdAt.toString(),
                "${installment.number}/${installment.ofTotal}",
                installment.dueDate.toString(),
                centsToDecimal(installment.amountCents),
                installment.paidAt?.toString().orEmpty(),
                status,
                order.observation.orEmpty(),
            )
        }
        return (listOf(header) + lines).joinToString("\n") { row ->
            row.joinToString(";") { it.csvCell() }
        } + "\n"
    }

    suspend fun suggestCustomers(prefix: String): List<CustomerEntity> {
        if (prefix.isBlank()) return emptyList()
        return customerDao.suggestByPrefix(prefix)
    }

    suspend fun findCustomerIdByName(name: String): Long? =
        customerDao.findByName(name)?.id

    private fun centsToDecimal(cents: Long): String {
        val sign = if (cents < 0) "-" else ""
        val abs = kotlin.math.abs(cents)
        return "$sign${abs / 100},${(abs % 100).toString().padStart(2, '0')}"
    }

    private fun String.csvCell(): String =
        "\"${replace("\"", "\"\"")}\""
}
