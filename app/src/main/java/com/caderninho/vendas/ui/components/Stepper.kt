package com.caderninho.vendas.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Stepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    max: Int = 6,
    min: Int = 1,
) {
    PaperStepper(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        max = max,
        min = min,
    )
}
