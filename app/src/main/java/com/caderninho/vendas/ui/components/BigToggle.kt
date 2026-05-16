package com.caderninho.vendas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Rule

data class ToggleOption<T>(val value: T, val label: String, val sub: String? = null)

@Composable
fun <T> BigToggle(
    options: List<ToggleOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { opt ->
            val isSel = opt.value == selected
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (isSel) Modifier.background(Ink)
                        else Modifier.background(PaperAlt).border(BorderStroke(1.dp, Rule), RoundedCornerShape(12.dp))
                    )
                    .clickable { onSelect(opt.value) }
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = opt.label,
                    color = if (isSel) Paper else Ink,
                    style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp),
                )
                if (opt.sub != null) {
                    Text(
                        text = opt.sub,
                        color = if (isSel) Paper.copy(alpha = 0.7f) else Ink.copy(alpha = 0.7f),
                        style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp),
                    )
                }
            }
        }
    }
}
