package com.caderninho.vendas.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.theme.LocalCaveatStyle

@Composable
fun BrandMark(
    modifier: Modifier = Modifier,
    size: TextUnit = 22.sp,
    color: Color = LocalContentColor.current,
) {
    Text(
        text = "caderninho",
        modifier = modifier,
        color = color,
        style = LocalCaveatStyle.current.copy(fontSize = size),
    )
}
