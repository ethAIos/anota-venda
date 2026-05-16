package com.caderninho.vendas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.theme.AmberInk
import com.caderninho.vendas.ui.theme.AmberSoft
import com.caderninho.vendas.ui.theme.AvatarDefaultBg
import com.caderninho.vendas.ui.theme.AvatarDefaultFg
import com.caderninho.vendas.ui.theme.AvatarGreenBg
import com.caderninho.vendas.ui.theme.AvatarRedBg
import com.caderninho.vendas.ui.theme.AvatarRedFg
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.NunitoFamily

enum class AvatarTone { DEFAULT, GREEN, RED, AMBER }

@Composable
fun InitialAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp,
    tone: AvatarTone = AvatarTone.DEFAULT,
) {
    val (bg, fg) = when (tone) {
        AvatarTone.DEFAULT -> AvatarDefaultBg to AvatarDefaultFg
        AvatarTone.GREEN -> AvatarGreenBg to Green
        AvatarTone.RED -> AvatarRedBg to AvatarRedFg
        AvatarTone.AMBER -> AmberSoft to AmberInk
    }
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initial,
            color = fg,
            style = TextStyle(
                fontFamily = NunitoFamily,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.4f).sp,
            ),
        )
    }
}
