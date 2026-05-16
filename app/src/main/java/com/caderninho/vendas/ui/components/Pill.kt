package com.caderninho.vendas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.theme.AmberInk
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.GreenSoft
import com.caderninho.vendas.ui.theme.Highlight
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.RedSoft

enum class PillTone { DEFAULT, RED, GREEN, AMBER }

@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    tone: PillTone = PillTone.DEFAULT,
) {
    val bg: Color
    val fg: Color
    when (tone) {
        PillTone.DEFAULT -> { bg = PaperAlt; fg = InkSoft }
        PillTone.RED -> { bg = RedSoft; fg = Red }
        PillTone.GREEN -> { bg = GreenSoft; fg = Green }
        PillTone.AMBER -> { bg = Highlight; fg = AmberInk }
    }
    Text(
        text = text,
        color = fg,
        modifier = modifier
            .clip(RoundedCornerShape(99.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        style = TextStyle(
            fontFamily = NunitoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.1.sp,
        ),
    )
}
