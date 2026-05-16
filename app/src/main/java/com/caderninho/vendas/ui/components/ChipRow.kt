package com.caderninho.vendas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipRow(
    chips: List<String>,
    selected: String?,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        chips.forEach { c ->
            val isSelected = c == selected
            val bg = if (isSelected) Ink else PaperAlt
            val fg = if (isSelected) Paper else Ink
            Text(
                text = c,
                color = fg,
                modifier = Modifier
                    .clip(RoundedCornerShape(99.dp))
                    .then(
                        if (isSelected) Modifier.background(bg)
                        else Modifier.background(bg).border(BorderStroke(1.dp, Rule), RoundedCornerShape(99.dp))
                    )
                    .clickable { onClick(c) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp),
            )
        }
    }
}
