package com.caderninho.vendas.ui.screens.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caderninho.vendas.data.db.entities.InstallmentEntity
import com.caderninho.vendas.data.prefs.UserPrefsRepository
import com.caderninho.vendas.data.repo.CustomerSummary
import com.caderninho.vendas.data.repo.SalesRepository
import com.caderninho.vendas.nav.Destinations
import com.caderninho.vendas.ui.components.AvatarTone
import com.caderninho.vendas.util.WhatsAppLauncher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: SalesRepository,
    private val prefs: UserPrefsRepository,
) : ViewModel() {

    val customerId: Long = savedStateHandle.get<String>(Destinations.ArgId)?.toLongOrNull() ?: -1L

    val summary: StateFlow<CustomerSummary?> = repo.observeCustomerSummary(customerId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    sealed class Event {
        object Deleted : Event()
    }

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private val _uiError = MutableStateFlow<String?>(null)
    val uiError: StateFlow<String?> = _uiError.asStateFlow()

    fun clearError() { _uiError.value = null }

    fun markPaid(installmentId: Long) {
        viewModelScope.launch { repo.markPaid(installmentId) }
    }

    fun nextOpenInstallment(): InstallmentEntity? {
        val s = summary.value ?: return null
        val all = s.orders.flatMap { it.installments }.filter { it.paidAt == null }
        return all.minByOrNull { it.dueDate }
    }

    suspend fun chargeMessage(): String? {
        val s = summary.value ?: return null
        val next = nextOpenInstallment() ?: return null
        val orderForInst = s.orders.firstOrNull { os -> os.installments.any { it.id == next.id } }?.order ?: return null
        val template = prefs.chargeTemplate.first()
        return WhatsAppLauncher.fillTemplate(
            template = template,
            customerName = s.customer.name,
            product = orderForInst.what,
            amountCents = next.amountCents,
            dueDate = next.dueDate,
        )
    }

    fun updateCustomer(name: String, phone: String?, tone: AvatarTone) {
        viewModelScope.launch {
            try {
                repo.updateCustomer(customerId, name, phone, tone)
            } catch (e: IllegalStateException) {
                _uiError.value = e.message
            }
        }
    }

    fun deleteCustomer() {
        viewModelScope.launch {
            repo.deleteCustomer(customerId)
            _events.emit(Event.Deleted)
        }
    }
}
