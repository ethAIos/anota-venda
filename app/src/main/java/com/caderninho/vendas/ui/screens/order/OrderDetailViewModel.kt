package com.caderninho.vendas.ui.screens.order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caderninho.vendas.data.repo.OrderDetail
import com.caderninho.vendas.data.repo.SalesRepository
import com.caderninho.vendas.nav.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: SalesRepository,
) : ViewModel() {

    val orderId: Long = savedStateHandle.get<String>(Destinations.ArgId)?.toLongOrNull() ?: -1L

    val detail: StateFlow<OrderDetail?> = repo.observeOrderDetail(orderId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    sealed class Event {
        object Closed : Event()
    }

    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private val _uiError = MutableStateFlow<String?>(null)
    val uiError: StateFlow<String?> = _uiError.asStateFlow()

    fun clearError() { _uiError.value = null }

    fun markPaid(installmentId: Long) {
        viewModelScope.launch { repo.markPaid(installmentId) }
    }

    fun unmarkPaid(installmentId: Long) {
        viewModelScope.launch { repo.unmarkPaid(installmentId) }
    }

    fun postponeInstallment(installmentId: Long, newDate: LocalDate) {
        viewModelScope.launch { repo.postpone(installmentId, newDate) }
    }

    fun updateInstallmentAmount(installmentId: Long, cents: Long) {
        viewModelScope.launch {
            try {
                repo.updateInstallmentAmount(installmentId, cents)
            } catch (e: IllegalStateException) {
                _uiError.value = e.message
            }
        }
    }

    fun updateOrderDetails(what: String, observation: String?) {
        viewModelScope.launch {
            repo.updateOrderDetails(orderId, what, observation)
        }
    }

    fun updateOrder(
        what: String,
        observation: String?,
        createdAt: LocalDate,
        totalCents: Long,
    ) {
        viewModelScope.launch {
            try {
                repo.updateOrder(orderId, what, observation, createdAt, totalCents)
            } catch (e: IllegalStateException) {
                _uiError.value = e.message
            }
        }
    }

    fun deleteOrder() {
        viewModelScope.launch {
            repo.deleteOrder(orderId)
            _events.emit(Event.Closed)
        }
    }
}
