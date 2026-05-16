package com.caderninho.vendas.ui.screens.payingtoday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caderninho.vendas.data.prefs.UserPrefsRepository
import com.caderninho.vendas.data.repo.PayingTodayRow
import com.caderninho.vendas.data.repo.SalesRepository
import com.caderninho.vendas.util.WhatsAppLauncher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class WeekSummary(
    val date: LocalDate,
    val count: Int = 0,
    val totalCents: Long = 0L,
)

data class PayingTodayState(
    val selectedDate: LocalDate = LocalDate.now(),
    val weekStart: LocalDate = startOfWeek(LocalDate.now()),
    val pickerOpen: Boolean = false,
    val searchOpen: Boolean = false,
    val query: String = "",
    val weekSummaries: List<WeekSummary> = emptyList(),
    val rows: List<PayingTodayRow> = emptyList(),
    val filteredRows: List<PayingTodayRow> = emptyList(),
    val totalCents: Long = 0L,
    val today: LocalDate = LocalDate.now(),
)

private data class PayingControls(
    val selectedDate: LocalDate,
    val weekStart: LocalDate,
    val pickerOpen: Boolean,
    val searchOpen: Boolean,
    val query: String,
)

@HiltViewModel
class PayingTodayViewModel @Inject constructor(
    private val repo: SalesRepository,
    private val prefs: UserPrefsRepository,
) : ViewModel() {

    private val today = LocalDate.now()
    private val selectedDate = MutableStateFlow(today)
    private val weekStart = MutableStateFlow(startOfWeek(today))
    private val pickerOpen = MutableStateFlow(false)
    private val searchOpen = MutableStateFlow(false)
    private val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val weekRows = weekStart.flatMapLatest { start ->
        repo.observeOpenDueBetween(start, start.plusDays(6))
    }

    private val controls = combine(
        selectedDate,
        weekStart,
        pickerOpen,
        searchOpen,
        query,
    ) { selectedDate, weekStart, pickerOpen, searchOpen, query ->
        PayingControls(
            selectedDate = selectedDate,
            weekStart = weekStart,
            pickerOpen = pickerOpen,
            searchOpen = searchOpen,
            query = query,
        )
    }

    val state: StateFlow<PayingTodayState> = controls
        .combine(weekRows) { controls, weekRows ->
            val rows = weekRows
                .filter { it.installment.dueDate == controls.selectedDate }
                .sortedWith(compareBy<PayingTodayRow> { it.customer.name.lowercase() }.thenBy { it.installment.id })
            val trimmedQuery = controls.query.trim()
            val filteredRows = if (trimmedQuery.isBlank()) {
                rows
            } else {
                rows.filter {
                    it.customer.name.contains(trimmedQuery, ignoreCase = true) ||
                        it.order.what.contains(trimmedQuery, ignoreCase = true)
                }
            }
            val weekSummaries = (0L..6L).map { offset ->
                val date = controls.weekStart.plusDays(offset)
                val dayRows = weekRows.filter { it.installment.dueDate == date }
                WeekSummary(
                    date = date,
                    count = dayRows.distinctBy { it.customer.id }.size,
                    totalCents = dayRows.sumOf { it.installment.amountCents },
                )
            }
            PayingTodayState(
                rows = rows,
                filteredRows = filteredRows,
                totalCents = rows.sumOf { it.installment.amountCents },
                today = today,
                selectedDate = controls.selectedDate,
                weekStart = controls.weekStart,
                pickerOpen = controls.pickerOpen,
                searchOpen = controls.searchOpen,
                query = controls.query,
                weekSummaries = weekSummaries,
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PayingTodayState(selectedDate = today, weekStart = startOfWeek(today), today = today),
        )

    fun togglePicker() {
        pickerOpen.update { !it }
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
        weekStart.value = startOfWeek(date)
    }

    fun previousWeek() {
        moveWeek(-7)
    }

    fun nextWeek() {
        moveWeek(7)
    }

    fun toggleSearch() {
        val shouldOpen = !searchOpen.value
        searchOpen.value = shouldOpen
        if (!shouldOpen) query.value = ""
    }

    fun updateQuery(value: String) {
        query.value = value
    }

    fun markPaid(installmentId: Long) {
        viewModelScope.launch { repo.markPaid(installmentId) }
    }

    fun postpone(installmentId: Long, days: Long = 7L) {
        viewModelScope.launch {
            val now = LocalDate.now()
            repo.postpone(installmentId, now.plusDays(days))
        }
    }

    suspend fun chargeMessage(row: PayingTodayRow): String {
        val template = prefs.chargeTemplate.first()
        return WhatsAppLauncher.fillTemplate(
            template = template,
            customerName = row.customer.name,
            product = row.order.what,
            amountCents = row.installment.amountCents,
            dueDate = row.installment.dueDate,
        )
    }

    private fun moveWeek(days: Long) {
        weekStart.update { it.plusDays(days) }
        selectedDate.update { it.plusDays(days) }
    }
}

private fun startOfWeek(date: LocalDate): LocalDate =
    date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
