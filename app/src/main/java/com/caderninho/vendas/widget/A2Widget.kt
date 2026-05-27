package com.caderninho.vendas.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
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
import com.caderninho.vendas.data.repo.PayingTodayRow
import com.caderninho.vendas.ui.components.formatBrl
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.GreenSoft
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.Rule
import com.caderninho.vendas.util.formatShortDay
import kotlinx.coroutines.flow.first
import java.time.LocalDate

class A2Widget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repo = context.widgetRepo()
        val today = LocalDate.now()
        val rows: List<PayingTodayRow> = runCatching {
            repo.observePayingToday(today).first()
        }.getOrElse { emptyList() }
        val total = rows.sumOf { it.installment.amountCents }
        provideContent { A2Content(rows = rows.take(3), total = total, today = today) }
    }
}

@Composable
private fun A2Content(rows: List<PayingTodayRow>, total: Long, today: LocalDate) {
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
                    "Pagam hoje",
                    style = TextStyle(
                        color = ColorProvider(Ink),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    ),
                )
                Spacer(modifier = GlanceModifier.size(6.dp))
                Text(
                    formatShortDay(today),
                    style = TextStyle(
                        color = ColorProvider(InkSoft),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
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
            Spacer(modifier = GlanceModifier.size(6.dp))
            if (rows.isEmpty()) {
                Column(
                    modifier = GlanceModifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Ninguém vence hoje.",
                        style = TextStyle(
                            color = ColorProvider(Ink),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                        ),
                    )
                    Text(
                        "Bom dia!",
                        style = TextStyle(
                            color = ColorProvider(Green),
                            fontStyle = FontStyle.Italic,
                            fontSize = 18.sp,
                        ),
                    )
                }
            } else {
                rows.forEachIndexed { index, row ->
                    A2Row(row)
                    if (index < rows.size - 1) {
                        Box(modifier = GlanceModifier.fillMaxWidth().size(1.dp).background(ColorProvider(Rule))) {}
                    }
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
                Box(modifier = GlanceModifier.fillMaxWidth().size(1.dp).background(ColorProvider(Rule))) {}
                Spacer(modifier = GlanceModifier.size(6.dp))
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "total a receber",
                        style = TextStyle(
                            color = ColorProvider(InkSoft),
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                        ),
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        formatBrl(total),
                        style = TextStyle(
                            color = ColorProvider(Green),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun A2Row(row: PayingTodayRow) {
    val context = LocalContext.current
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .clickable(actionStartActivity(deepLinkIntent(context, "receive/${row.installment.id}")))
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            row.customer.name,
            modifier = GlanceModifier.defaultWeight(),
            maxLines = 1,
            style = TextStyle(
                color = ColorProvider(Ink),
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            ),
        )
        Text(
            formatBrl(row.installment.amountCents),
            style = TextStyle(
                color = ColorProvider(Ink),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            ),
        )
        Spacer(modifier = GlanceModifier.size(8.dp))
        Box(
            modifier = GlanceModifier
                .size(40.dp)
                .background(ColorProvider(GreenSoft))
                .cornerRadius(12.dp)
                .clickable(actionRunCallback<MarkPaidAction>(
                    actionParametersOf(MarkPaidAction.IdKey to row.installment.id)
                )),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "✓",
                style = TextStyle(
                    color = ColorProvider(Green),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                ),
            )
        }
    }
}

class MarkPaidAction : ActionCallback {
    companion object {
        val IdKey = ActionParameters.Key<Long>("installmentId")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val id = parameters[IdKey] ?: return
        context.widgetRepo().markPaid(id)
        A2Widget().updateAll(context)
        A3Widget().updateAll(context)
    }
}

class A2WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = A2Widget()
}
