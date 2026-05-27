package com.caderninho.vendas.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caderninho.vendas.ui.theme.Green
import com.caderninho.vendas.ui.theme.Ink
import com.caderninho.vendas.ui.theme.InkSoft
import com.caderninho.vendas.ui.theme.NunitoFamily
import com.caderninho.vendas.ui.theme.PaperAlt
import com.caderninho.vendas.ui.theme.Rule
import com.caderninho.vendas.ui.theme.RuleStrong

@Composable
fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier.padding(bottom = 6.dp),
        color = InkSoft,
        style = TextStyle(
            fontFamily = NunitoFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.6.sp,
        ),
    )
}

@Composable
fun PaperInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    big: Boolean = false,
    prefix: String? = null,
    keyboard: KeyboardType = KeyboardType.Text,
    suggestion: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val mainStyle = TextStyle(
        fontFamily = NunitoFamily,
        fontWeight = if (big) FontWeight.ExtraBold else FontWeight.SemiBold,
        fontSize = if (big) 32.sp else 18.sp,
        color = if (value.isNotEmpty()) Ink else InkSoft,
        letterSpacing = 0.sp,
    )
    Column(modifier) {
        Row(
            modifier = Modifier
                .padding(bottom = 6.dp)
                .drawBehind {
                    drawLine(
                        color = RuleStrong,
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.5.dp.toPx(),
                    )
                },
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (prefix != null) {
                Text(
                    text = prefix,
                    color = InkSoft,
                    style = TextStyle(
                        fontFamily = NunitoFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (big) 28.sp else 17.sp,
                    ),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = mainStyle,
                cursorBrush = SolidColor(Green),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboard),
                visualTransformation = visualTransformation,
                modifier = Modifier.padding(bottom = 6.dp),
                decorationBox = { inner ->
                    if (value.isEmpty() && placeholder.isNotEmpty()) {
                        Text(text = placeholder, style = mainStyle)
                    }
                    inner()
                },
            )
        }
        if (suggestion != null && value.isEmpty()) {
            Text(
                text = suggestion,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(99.dp))
                    .background(PaperAlt)
                    .border(BorderStroke(1.dp, Rule), RoundedCornerShape(99.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                color = InkSoft,
                style = TextStyle(fontFamily = NunitoFamily, fontWeight = FontWeight.SemiBold, fontSize = 13.sp),
            )
        }
    }
}
