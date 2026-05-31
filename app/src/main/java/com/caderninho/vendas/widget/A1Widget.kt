package com.caderninho.vendas.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.text.Text
import com.caderninho.vendas.MainActivity

class A1Widget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { A1Content() }
    }
}

@Composable
private fun A1Content() {
    val context = LocalContext.current
    WidgetCard(
        padding = WidgetSmallPadding,
        action = actionStartActivity(deepLinkIntent(context, "newsale")),
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                WidgetAddMark()
                Spacer(modifier = GlanceModifier.defaultWeight())
                WidgetBrand()
            }
            Spacer(modifier = GlanceModifier.defaultWeight())
            Column {
                Text(
                    "Anotar venda",
                    style = widgetTitleStyle(fontSize = 16.sp),
                )
                Text(
                    "toque para registrar",
                    style = widgetMetaStyle(fontSize = 11.sp),
                )
            }
        }
    }
}

internal fun deepLinkIntent(context: Context, path: String): Intent =
    Intent(Intent.ACTION_VIEW, Uri.parse("caderninho://$path"), context, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

class A1WidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = A1Widget()
}
