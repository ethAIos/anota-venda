package com.caderninho.vendas.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private fun DrawScope.dotGrid(
    spacingPx: Float,
    radiusPx: Float,
    color: Color,
    offsetX: Float = 0f,
    offsetY: Float = 0f,
) {
    var y = offsetY
    while (y < size.height) {
        var x = offsetX
        while (x < size.width) {
            drawCircle(color = color, radius = radiusPx, center = Offset(x, y))
            x += spacingPx
        }
        y += spacingPx
    }
}

fun Modifier.paperBackground(): Modifier = drawBehind {
    drawRect(Paper)
    dotGrid(
        spacingPx = 6.dp.toPx(),
        radiusPx = 0.6.dp.toPx(),
        color = Color(0x14B4A078),
    )
    dotGrid(
        spacingPx = 11.dp.toPx(),
        radiusPx = 0.7.dp.toPx(),
        color = Color(0x0EB4A078),
        offsetX = 3.dp.toPx(),
        offsetY = 4.dp.toPx(),
    )
}

fun Modifier.ruled(spacing: Dp = 32.dp, color: Color = Rule): Modifier = drawBehind {
    val step = spacing.toPx()
    var y = step
    while (y < size.height) {
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f,
        )
        y += step
    }
}

fun Modifier.bottomRule(color: Color = RuleStrong): Modifier = drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, size.height),
        end = Offset(size.width, size.height),
        strokeWidth = 1.5.dp.toPx(),
    )
}
