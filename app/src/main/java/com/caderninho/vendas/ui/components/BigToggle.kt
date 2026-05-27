package com.caderninho.vendas.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class ToggleOption<T>(val value: T, val label: String, val sub: String? = null)

@Composable
fun <T> BigToggle(
    options: List<ToggleOption<T>>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    PaperSegmentedChoice(
        options = options,
        selected = selected,
        onSelect = onSelect,
        modifier = modifier,
    )
}
