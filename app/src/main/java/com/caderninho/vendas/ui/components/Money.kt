package com.caderninho.vendas.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.theme.NunitoFamily
import java.text.NumberFormat
import java.util.Locale

private val brFormatter: NumberFormat by lazy {
    NumberFormat.getNumberInstance(Locale("pt", "BR")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
}

fun formatBrl(cents: Long): String {
    val sign = if (cents < 0) "−" else ""
    val abs = kotlin.math.abs(cents)
    val reais = abs / 100
    val centsPart = (abs % 100).toString().padStart(2, '0')
    val reaisFmt = NumberFormat.getIntegerInstance(Locale("pt", "BR")).format(reais)
    return "${sign}R$ $reaisFmt,$centsPart"
}

@Composable
fun Money(
    cents: Long,
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
    size: TextUnit = 16.sp,
    weight: FontWeight = FontWeight.ExtraBold,
) {
    Text(
        text = formatBrl(cents),
        modifier = modifier,
        color = color,
        style = TextStyle(
            fontFamily = NunitoFamily,
            fontWeight = weight,
            fontSize = size,
            letterSpacing = 0.sp,
            fontFeatureSettings = "tnum",
        ),
    )
}
