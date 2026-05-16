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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.caderninho.vendas.data.model.OrderStatus
import com.caderninho.vendas.data.repo.OpenInstallmentRow
import com.caderninho.vendas.ui.components.formatBrl
import com.caderninho.vendas.ui.theme.Amber
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.RuleStrong
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
        provideContent { A3Content(rows.take(4), total) }
    }
}

@Composable
private fun A3Content(rows: List<OpenInstallmentRow>, total: Long) {
    val context = LocalContext.current
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Paper))
            .cornerRadius(22.dp)
            .padding(14.dp)
            .clickable(actionStartActivity(deepLinkIntent(context, "payingtoday"))),
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Em aberto",
                    style = TextStyle(
                        color = ColorProvider(Ink),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    ),
                )
                Spacer(modifier = GlanceModifier.size(8.dp))
                Text(
                    formatBrl(total),
                    style = TextStyle(
                        color = ColorProvider(InkSoft),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                    ),
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    "caderninho",
                    style = TextStyle(
                        color = ColorProvider(InkSoft),
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                    ),
                )
            }
            Spacer(modifier = GlanceModifier.size(4.dp))
            if (rows.isEmpty()) {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Tudo em dia.",
                        style = TextStyle(
                            color = ColorProvider(Ink),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        ),
                    )
                    Text(
                        "Aproveita!",
                        style = TextStyle(
                            color = ColorProvider(Green),
                            fontStyle = FontStyle.Italic,
                            fontSize = 18.sp,
                        ),
                    )
                }
            } else {
                rows.forEach { r -> A3Row(r) }
            }
        }
    }
}

@Composable
private fun A3Row(row: OpenInstallmentRow) {
    val context = LocalContext.current
    val dotColor = when (row.status) {
        OrderStatus.OVERDUE -> Red
        OrderStatus.DUE_TODAY -> Amber
        OrderStatus.PAID -> Green
        OrderStatus.OPEN -> RuleStrong
    }
    val statusText = describeDue(row.installment.dueDate)
    val statusColor = if (row.status == OrderStatus.OVERDUE) Red else InkSoft
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(actionStartActivity(deepLinkIntent(context, "customer/${row.customer.id}")))
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = GlanceModifier.size(6.dp).background(ColorProvider(dotColor)).cornerRadius(3.dp)) {}
        Spacer(modifier = GlanceModifier.size(8.dp))
        Text(
            row.customer.name,
            modifier = GlanceModifier.defaultWeight(),
            maxLines = 1,
            style = TextStyle(
                color = ColorProvider(Ink),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            ),
        )
        Spacer(modifier = GlanceModifier.size(4.dp))
        Text(
            statusText,
            style = TextStyle(
                color = ColorProvider(statusColor),
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
            ),
        )
        Spacer(modifier = GlanceModifier.size(6.dp))
        Text(
            formatBrl(row.installment.amountCents),
            style = TextStyle(
                color = ColorProvider(Ink),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            ),
        )
    }
}

class A3WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = A3Widget()
}
