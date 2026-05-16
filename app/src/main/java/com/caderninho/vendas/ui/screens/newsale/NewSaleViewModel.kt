package com.caderninho.vendas.ui.screens.newsale

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caderninho.vendas.data.db.entities.CustomerEntity
import com.caderninho.vendas.data.model.PaymentMode
import com.caderninho.vendas.data.repo.SalesRepository
import com.caderninho.vendas.nav.Destinations
import com.caderninho.vendas.ui.components.digitsToCents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class NewSaleState(
    val customerName: String = "",
    val customerPhone: String = "",
    val suggestions: List<CustomerEntity> = emptyList(),
    val selectedSuggestion: CustomerEntity? = null,
    val what: String = "",
    val amountText: String = "",
    val paymentMode: PaymentMode = PaymentMode.FIADO,
    val dueDate: LocalDate = LocalDate.now().plusDays(7),
    val installments: Int = 1,
    val savedOrderId: Long? = null,
    val savedCustomerId: Long? = null,
    val savedCustomerName: String? = null,
    val savedDueDate: LocalDate? = null,
    val savedAmountCents: Long? = null,
)

@HiltViewModel
class NewSaleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: SalesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NewSaleState())
    val state: StateFlow<NewSaleState> = _state.asStateFlow()

    init {
        val customerId = savedStateHandle.get<Long>(Destinations.ArgCustomerId) ?: -1L
        if (customerId > 0L) {
            viewModelScope.launch {
                repo.getCustomer(customerId)?.let { customer ->
                    _state.update {
                        it.copy(
                            customerName = customer.name,
                            customerPhone = customer.phone.orEmpty(),
                            selectedSuggestion = customer,
                            suggestions = emptyList(),
                        )
                    }
                }
            }
        }
    }

    fun onCustomerName(value: String) {
        _state.update { it.copy(customerName = value, selectedSuggestion = null) }
        viewModelScope.launch {
            val suggestions = repo.suggestCustomers(value)
            _state.update { it.copy(suggestions = suggestions) }
        }
    }

    fun selectSuggestion(c: CustomerEntity) {
        _state.update {
            it.copy(
                customerName = c.name,
                customerPhone = c.phone.orEmpty(),
                selectedSuggestion = c,
                suggestions = emptyList(),
            )
        }
    }

    fun onCustomerPhone(value: String) {
        _state.update { it.copy(customerPhone = value) }
    }

    fun onWhat(value: String) {
        _state.update { it.copy(what = value) }
    }

    fun onAmount(value: String) {
        // Estado guarda apenas dígitos (centavos); formatação BR é via VisualTransformation.
        val cleaned = value.filter { it.isDigit() }.take(12)
        _state.update { it.copy(amountText = cleaned) }
    }

    fun onPaymentMode(mode: PaymentMode) {
        _state.update { it.copy(paymentMode = mode) }
    }

    fun onDueDate(date: LocalDate) {
        _state.update { it.copy(dueDate = date) }
    }

    fun onInstallments(value: Int) {
        _state.update { it.copy(installments = value.coerceIn(1, 12)) }
    }

    fun save(onSaved: (Long) -> Unit) {
        val s = _state.value
        val cents = digitsToCents(s.amountText)
        if (cents <= 0L || s.customerName.isBlank() || s.what.isBlank()) return
        viewModelScope.launch {
            val orderId = repo.recordSale(
                customerName = s.customerName.trim(),
                phone = s.customerPhone.trim().takeIf { it.isNotBlank() },
                what = s.what.trim(),
                totalCents = cents,
                paymentMode = s.paymentMode,
                firstDueDate = if (s.paymentMode == PaymentMode.AVISTA) LocalDate.now() else s.dueDate,
                installmentCount = if (s.paymentMode == PaymentMode.AVISTA) 1 else s.installments,
            )
            val savedCustomerId = s.selectedSuggestion?.id
                ?: repo.findCustomerIdByName(s.customerName.trim())
            _state.update {
                it.copy(
                    savedOrderId = orderId,
                    savedCustomerId = savedCustomerId,
                    savedCustomerName = s.customerName.trim(),
                    savedDueDate = s.dueDate,
                    savedAmountCents = cents,
                )
            }
            onSaved(orderId)
        }
    }

    fun reset() {
        _state.value = NewSaleState()
    }
}
