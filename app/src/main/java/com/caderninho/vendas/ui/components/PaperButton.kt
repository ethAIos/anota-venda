package com.caderninho.vendas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.theme.CaderninhoShapes
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.GreenSoft
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.Paper
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Red
import com.caderninho.vendas.ui.theme.RedSoft
import com.caderninho.vendas.ui.theme.Rule

enum class PrimaryColor { GREEN, RED }

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: PrimaryColor = PrimaryColor.GREEN,
    fillWidth: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val container = if (color == PrimaryColor.GREEN) Green else Red
    Button(
        onClick = onClick,
        modifier = if (fillWidth) modifier.fillMaxWidth() else modifier,
        shape = CaderninhoShapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Paper,
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (leadingIcon != null) leadingIcon()
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = NunitoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                ),
            )
        }
    }
}

enum class GhostColor { INK, GREEN, RED, PAPER }

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: GhostColor = GhostColor.INK,
    fillWidth: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
) {
    val (bg, fg, border) = when (color) {
        GhostColor.INK -> Triple(androidx.compose.ui.graphics.Color.Transparent, Ink, Rule)
        GhostColor.GREEN -> Triple(GreenSoft, Green, androidx.compose.ui.graphics.Color.Transparent)
        GhostColor.RED -> Triple(RedSoft, Red, androidx.compose.ui.graphics.Color.Transparent)
        GhostColor.PAPER -> Triple(PaperAlt, Ink, Rule)
    }
    OutlinedButton(
        onClick = onClick,
        modifier = if (fillWidth) modifier.fillMaxWidth() else modifier,
        shape = CaderninhoShapes.small,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = bg,
            contentColor = fg,
        ),
        border = BorderStroke(1.dp, border),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (leadingIcon != null) leadingIcon()
            Text(
                text = text,
                style = TextStyle(
                    fontFamily = NunitoFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                ),
            )
        }
    }
}
