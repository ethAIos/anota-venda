package com.caderninho.vendas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.caderninho.vendas.ui.theme.Amber
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.RuleStrong

enum class DotTone { DEFAULT, RED, AMBER, GREEN }

@Composable
fun StatusDot(
    tone: DotTone,
    modifier: Modifier = Modifier,
    size: Dp = 8.dp,
) {
    val color: Color = when (tone) {
        DotTone.DEFAULT -> RuleStrong
        DotTone.RED -> Red
        DotTone.AMBER -> Amber
        DotTone.GREEN -> Green
    }
    Box(
        modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
    )
}
