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
import androidx.glance.text.Text
import com.caderninho.vendas.data.repo.PayingTodayRow
import com.caderninho.vendas.ui.components.formatBrl
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.util.formatShortDay
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class A2Widget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val expired = context.isDemoExpired()
        if (expired) {
            provideContent { A2ExpiredContent() }
            return
        }
        val repo = context.widgetRepo()
        val today = LocalDate.now()
        val rows: List<PayingTodayRow> = runCatching {
            repo.observePayingToday(today).first()
        }.getOrElse { emptyList() }
        val total = rows.sumOf { it.installment.amountCents }
        provideContent {
            A2Content(rows = rows.take(3), total = total, today = today, rowCount = rows.size)
        }
    }
}

@Composable
private fun A2ExpiredContent() {
    WidgetCard {
        WidgetExpiredState()
    }
}

@Composable
private fun A2Content(
    rows: List<PayingTodayRow>,
    total: Long,
    today: LocalDate,
    rowCount: Int,
) {
    val context = LocalContext.current
    WidgetCard(
        action = actionStartActivity(deepLinkIntent(context, "payingtoday")),
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            WidgetHeader("Pagam hoje", formatShortDay(today))
            Spacer(modifier = GlanceModifier.size(6.dp))
            if (rows.isEmpty()) {
                WidgetEmptyState("Ninguém vence hoje.", "Bom dia!")
            } else {
                rows.forEachIndexed { index, row ->
                    A2Row(row)
                    if (index < rows.size - 1) {
                        WidgetDivider()
                    }
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
                WidgetDivider(strong = true)
                Spacer(modifier = GlanceModifier.size(6.dp))
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        paymentCountLabel(rowCount),
                        style = widgetMetaStyle(fontSize = 11.sp),
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        formatBrl(total),
                        style = widgetMoneyStyle(color = Green, fontSize = 15.sp),
                    )
                }
            }
        }
    }
}

@Composable
private fun A2Row(row: PayingTodayRow) {
    val context = LocalContext.current
    val receiveAction = actionStartActivity(deepLinkIntent(context, "receive/${row.installment.id}"))
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(receiveAction)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            row.customer.name,
            modifier = GlanceModifier.defaultWeight(),
            maxLines = 1,
            style = widgetRowTitleStyle(fontSize = 13.sp),
        )
        Text(
            formatBrl(row.installment.amountCents),
            style = widgetMoneyStyle(fontSize = 13.sp),
        )
        Spacer(modifier = GlanceModifier.size(8.dp))
        WidgetReceiveChip(receiveAction)
    }
}

private fun paymentCountLabel(count: Int): String =
    if (count == 1) "1 cobrança hoje" else "$count cobranças hoje"

class A2WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = A2Widget()
}
