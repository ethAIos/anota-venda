package com.caderninho.vendas.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import com.caderninho.vendas.data.model.OrderStatus
import com.caderninho.vendas.data.repo.OpenInstallmentRow
import com.caderninho.vendas.ui.components.formatBrl
import com.caderninho.vendas.util.describeDue
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class A3Widget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = context.widgetRepo()
        val today = LocalDate.now()
        val rows = runCatching {
            repo.observeOpenWithDetail(today).first()
        }.getOrElse { emptyList() }
        val total = rows.sumOf { it.installment.amountCents }
        provideContent { A3Content(rows.sortedForTriage().take(4), total, today) }
    }
}

@Composable
private fun A3Content(rows: List<OpenInstallmentRow>, total: Long, today: LocalDate) {
    val context = LocalContext.current
    WidgetCard(
        action = actionStartActivity(deepLinkIntent(context, "payingtoday")),
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            WidgetHeader("Em aberto", formatBrl(total), widgetMoneyStyle(fontSize = 12.sp))
            Spacer(modifier = GlanceModifier.size(4.dp))
            if (rows.isEmpty()) {
                WidgetEmptyState("Tudo em dia.", "Aproveita!")
            } else {
                rows.forEachIndexed { index, row ->
                    A3Row(row, today)
                    if (index < rows.size - 1) {
                        WidgetDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun A3Row(row: OpenInstallmentRow, today: LocalDate) {
    val context = LocalContext.current
    val receiveAction = actionStartActivity(deepLinkIntent(context, "receive/${row.installment.id}"))
    val statusText = describeDue(row.installment.dueDate, today)
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(receiveAction)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WidgetStatusDot(row.status)
        Spacer(modifier = GlanceModifier.size(8.dp))
        Text(
            row.customer.name,
            modifier = GlanceModifier.defaultWeight(),
            maxLines = 1,
            style = widgetRowTitleStyle(fontSize = 12.sp),
        )
        Spacer(modifier = GlanceModifier.size(4.dp))
        Text(
            statusText,
            style = widgetMetaStyle(
                color = widgetStatusTextColor(row.status),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
        Spacer(modifier = GlanceModifier.size(6.dp))
        Text(
            formatBrl(row.installment.amountCents),
            style = widgetMoneyStyle(fontSize = 12.sp),
        )
        Spacer(modifier = GlanceModifier.size(4.dp))
        Text(
            ">",
            style = widgetMetaStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold),
        )
    }
}

private fun List<OpenInstallmentRow>.sortedForTriage(): List<OpenInstallmentRow> =
    sortedWith(
        compareBy<OpenInstallmentRow> { triagePriority(it.status) }
            .thenBy { it.installment.dueDate }
            .thenBy { it.installment.id },
    )

private fun triagePriority(status: OrderStatus): Int =
    when (status) {
        OrderStatus.OVERDUE -> 0
        OrderStatus.DUE_TODAY -> 1
        OrderStatus.OPEN -> 2
        OrderStatus.PAID -> 3
    }

class A3WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = A3Widget()
}
