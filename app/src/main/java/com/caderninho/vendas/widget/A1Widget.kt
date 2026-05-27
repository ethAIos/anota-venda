package com.caderninho.vendas.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.caderninho.vendas.MainActivity
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.Paper

class A1Widget : GlanceAppWidget() {
    override val sizeMode: SizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent { A1Content() }
    }
}

@Composable
private fun A1Content() {
    val context = LocalContext.current
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Paper))
            .cornerRadius(22.dp)
            .padding(12.dp)
            .clickable(actionStartActivity(deepLinkIntent(context, "newsale"))),
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            Row(modifier = GlanceModifier.fillMaxWidth()) {
                Box(
                    modifier = GlanceModifier
                        .size(44.dp)
                        .background(ColorProvider(Green))
                        .cornerRadius(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "+",
                        style = TextStyle(
                            color = ColorProvider(Paper),
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                        ),
                    )
                }
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    "caderninho",
                    style = TextStyle(
                        color = ColorProvider(InkSoft),
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                    ),
                )
            }
            Spacer(modifier = GlanceModifier.defaultWeight())
            Column {
                Text(
                    "Anotar venda",
                    style = TextStyle(
                        color = ColorProvider(Ink),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                )
                Text(
                    "toque para registrar",
                    style = TextStyle(
                        color = ColorProvider(InkSoft),
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                    ),
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
