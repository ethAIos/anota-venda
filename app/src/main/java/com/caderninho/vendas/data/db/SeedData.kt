package com.caderninho.vendas.data.db

import com.caderninho.vendas.data.db.entities.CustomerEntity
import com.caderninho.vendas.data.db.entities.InstallmentEntity
import com.caderninho.vendas.data.db.entities.OrderEntity
import com.caderninho.vendas.data.model.PaymentMode
import com.caderninho.vendas.data.prefs.UserPrefsRepository
import com.caderninho.vendas.ui.components.AvatarTone
import java.time.LocalDate

object SeedData {

    suspend fun populateIfEmpty(
        db: CaderninhoDatabase,
        prefs: UserPrefsRepository,
        today: LocalDate = LocalDate.now(),
    ) {
        if (prefs.isDemoSeeded()) return

        val customerDao = db.customerDao()
        val orderDao = db.orderDao()
        val installmentDao = db.installmentDao()

        // Migração: se já existe qualquer cliente (versão anterior),
        // marca a flag e não reseeda.
        if (customerDao.getAll().isNotEmpty()) {
            prefs.setDemoSeeded(true)
            return
        }

        val maria = CustomerEntity(name = "Maria Souza", phone = "+5511987654321", avatarTone = AvatarTone.AMBER)
        val joana = CustomerEntity(name = "Joana Pereira", phone = "+5511987654322", avatarTone = AvatarTone.DEFAULT)
        val carla = CustomerEntity(name = "Carla Lima", phone = "+5511987654323", avatarTone = AvatarTone.AMBER)
        val bia = CustomerEntity(name = "Bia Almeida", phone = "+5511987654324", avatarTone = AvatarTone.RED)
        val ana = CustomerEntity(name = "Ana Ribeiro", phone = "+5511987654325", avatarTone = AvatarTone.DEFAULT)

        val mariaId = customerDao.upsert(maria)
        val joanaId = customerDao.upsert(joana)
        val carlaId = customerDao.upsert(carla)
        val biaId = customerDao.upsert(bia)
        val anaId = customerDao.upsert(ana)

        // Maria: Batom Avon vermelho - vence hoje, 1x R$ 45
        val mariaOrderId = orderDao.insert(OrderEntity(
            customerId = mariaId,
            what = "Batom Avon vermelho",
            createdAt = today.minusDays(5),
            totalCents = 4500,
            paymentMode = PaymentMode.FIADO,
        ))
        installmentDao.insertAll(listOf(
            InstallmentEntity(orderId = mariaOrderId, number = 1, ofTotal = 1, dueDate = today, amountCents = 4500),
        ))

        // Maria: pedido pago anterior (Perfume Natura)
        val mariaPaidOrderId = orderDao.insert(OrderEntity(
            customerId = mariaId,
            what = "Perfume Natura",
            createdAt = today.minusDays(17),
            totalCents = 9000,
            paymentMode = PaymentMode.FIADO,
        ))
        installmentDao.insertAll(listOf(
            InstallmentEntity(
                orderId = mariaPaidOrderId, number = 1, ofTotal = 1,
                dueDate = today.minusDays(12), amountCents = 9000,
                paidAt = today.minusDays(12),
            ),
        ))

        // Maria: salgado para festa - atrasado
        val mariaOverdueOrderId = orderDao.insert(OrderEntity(
            customerId = mariaId,
            what = "Salgado para festa",
            createdAt = today.minusDays(22),
            totalCents = 6000,
            paymentMode = PaymentMode.FIADO,
        ))
        installmentDao.insertAll(listOf(
            InstallmentEntity(
                orderId = mariaOverdueOrderId, number = 1, ofTotal = 1,
                dueDate = today.minusDays(22), amountCents = 6000,
            ),
        ))

        // Joana: Panela Tupperware - 3x R$ 30, 1ª já paga
        val joanaOrderId = orderDao.insert(OrderEntity(
            customerId = joanaId,
            what = "Panela Tupperware",
            createdAt = today.minusDays(22),
            totalCents = 9000,
            paymentMode = PaymentMode.FIADO,
            observation = "entregar na casa dela, perto do mercado da Vila",
        ))
        installmentDao.insertAll(listOf(
            InstallmentEntity(
                orderId = joanaOrderId, number = 1, ofTotal = 3,
                dueDate = today.minusDays(22), amountCents = 3000,
                paidAt = today.minusDays(22),
            ),
            InstallmentEntity(
                orderId = joanaOrderId, number = 2, ofTotal = 3,
                dueDate = today, amountCents = 3000,
            ),
            InstallmentEntity(
                orderId = joanaOrderId, number = 3, ofTotal = 3,
                dueDate = today.plusDays(30), amountCents = 3000,
            ),
        ))

        // Carla: Blusa estampada - vence hoje
        val carlaOrderId = orderDao.insert(OrderEntity(
            customerId = carlaId,
            what = "Blusa estampada",
            createdAt = today.minusDays(7),
            totalCents = 2200,
            paymentMode = PaymentMode.FIADO,
        ))
        installmentDao.insertAll(listOf(
            InstallmentEntity(orderId = carlaOrderId, number = 1, ofTotal = 1, dueDate = today, amountCents = 2200),
        ))

        // Bia: atrasado 2 dias
        val biaOrderId = orderDao.insert(OrderEntity(
            customerId = biaId,
            what = "Sandália de couro",
            createdAt = today.minusDays(15),
            totalCents = 6000,
            paymentMode = PaymentMode.FIADO,
        ))
        installmentDao.insertAll(listOf(
            InstallmentEntity(
                orderId = biaOrderId, number = 1, ofTotal = 1,
                dueDate = today.minusDays(2), amountCents = 6000,
            ),
        ))

        // Ana: vence em 5 dias
        val anaOrderId = orderDao.insert(OrderEntity(
            customerId = anaId,
            what = "Conjunto de potes",
            createdAt = today.minusDays(2),
            totalCents = 8000,
            paymentMode = PaymentMode.FIADO,
        ))
        installmentDao.insertAll(listOf(
            InstallmentEntity(
                orderId = anaOrderId, number = 1, ofTotal = 1,
                dueDate = today.plusDays(3), amountCents = 8000,
            ),
        ))

        prefs.setDemoSeeded(true)
    }
}
