package com.caderninho.vendas.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
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
import com.caderninho.vendas.ui.theme.Amber
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.GreenSoft
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.Rule
import com.caderninho.vendas.ui.theme.RuleStrong

internal val WidgetSmallPadding: Dp = 12.dp
internal val WidgetRegularPadding: Dp = 14.dp

@Composable
internal fun WidgetCard(
    modifier: GlanceModifier = GlanceModifier,
    padding: Dp = WidgetRegularPadding,
    action: Action? = null,
    content: @Composable () -> Unit,
) {
    var cardModifier = modifier
        .fillMaxSize()
        .background(ColorProvider(Paper))
        .cornerRadius(22.dp)
        .padding(padding)
    if (action != null) {
        cardModifier = cardModifier.clickable(action)
    }
    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
internal fun WidgetHeader(
    title: String,
    subtitle: String? = null,
    subtitleStyle: TextStyle = widgetMetaStyle(fontSize = 11.sp),
) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = widgetTitleStyle())
        if (subtitle != null) {
            Spacer(modifier = GlanceModifier.size(7.dp))
            Text(subtitle, style = subtitleStyle)
        }
        Spacer(modifier = GlanceModifier.defaultWeight())
        WidgetBrand()
    }
}

@Composable
internal fun WidgetBrand() {
    Text(
        "caderninho",
        style = TextStyle(
            color = ColorProvider(InkSoft),
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
        ),
    )
}

@Composable
internal fun WidgetAddMark() {
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
}

@Composable
internal fun WidgetDivider(strong: Boolean = false) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .size(1.dp)
            .background(ColorProvider(if (strong) RuleStrong else Rule)),
    ) {}
}

@Composable
internal fun WidgetEmptyState(title: String, accent: String) {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(title, style = widgetTitleStyle(fontSize = 14.sp))
        Text(
            accent,
            style = TextStyle(
                color = ColorProvider(Green),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
            ),
        )
    }
}

@Composable
internal fun WidgetExpiredState() {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Demo expirada",
            style = widgetTitleStyle(fontSize = 14.sp),
        )
        Text(
            "Tempo limite encerrado.",
            style = TextStyle(
                color = ColorProvider(Red),
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
            ),
        )
    }
}

@Composable
internal fun WidgetStatusDot(status: OrderStatus) {
    Box(
        modifier = GlanceModifier
            .size(6.dp)
            .background(ColorProvider(widgetStatusColor(status)))
            .cornerRadius(3.dp),
    ) {}
}

@Composable
internal fun WidgetReceiveChip(action: Action) {
    Box(
        modifier = GlanceModifier
            .background(ColorProvider(GreenSoft))
            .cornerRadius(12.dp)
            .clickable(action)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "Recebi",
            style = TextStyle(
                color = ColorProvider(Green),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
            ),
        )
    }
}

internal fun widgetTitleStyle(fontSize: TextUnit = 15.sp): TextStyle =
    TextStyle(
        color = ColorProvider(Ink),
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
    )

internal fun widgetRowTitleStyle(fontSize: TextUnit = 13.sp): TextStyle =
    TextStyle(
        color = ColorProvider(Ink),
        fontWeight = FontWeight.Medium,
        fontSize = fontSize,
    )

internal fun widgetMetaStyle(
    color: Color = InkSoft,
    fontSize: TextUnit = 11.sp,
    fontWeight: FontWeight = FontWeight.Medium,
): TextStyle =
    TextStyle(
        color = ColorProvider(color),
        fontWeight = fontWeight,
        fontSize = fontSize,
    )

internal fun widgetMoneyStyle(
    color: Color = Ink,
    fontSize: TextUnit = 13.sp,
): TextStyle =
    TextStyle(
        color = ColorProvider(color),
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
    )

internal fun widgetStatusColor(status: OrderStatus): Color =
    when (status) {
        OrderStatus.OVERDUE -> Red
        OrderStatus.DUE_TODAY -> Amber
        OrderStatus.PAID -> Green
        OrderStatus.OPEN -> RuleStrong
    }

internal fun widgetStatusTextColor(status: OrderStatus): Color =
    when (status) {
        OrderStatus.OVERDUE -> Red
        OrderStatus.DUE_TODAY -> Amber
        else -> InkSoft
    }
