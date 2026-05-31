package com.caderninho.vendas.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.caderninho.vendas.data.repo.OpenInstallmentRow
import com.caderninho.vendas.data.repo.SalesRepository
import com.caderninho.vendas.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetRefreshObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    @ApplicationScope private val scope: CoroutineScope,
    private val repo: SalesRepository,
) {
    fun start() {
        scope.launch {
            repo.observeOpenWithDetail()
                .map { rows -> rows.map(::WidgetRefreshRowSnapshot) }
                .distinctUntilChanged()
                .drop(1)
                .collect {
                    A1Widget().updateAll(context)
                    A2Widget().updateAll(context)
                    A3Widget().updateAll(context)
                }
        }
    }
}

private data class WidgetRefreshRowSnapshot(
    val id: Long,
    val customerName: String,
    val dueDate: LocalDate,
    val amountCents: Long,
    val paidAt: LocalDate?,
) {
    constructor(row: OpenInstallmentRow) : this(
        id = row.installment.id,
        customerName = row.customer.name,
        dueDate = row.installment.dueDate,
        amountCents = row.installment.amountCents,
        paidAt = row.installment.paidAt,
    )
}
