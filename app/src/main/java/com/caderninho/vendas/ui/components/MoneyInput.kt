package com.caderninho.vendas.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

private val PT_BR = Locale("pt", "BR")
private val INTEGER_FORMATTER = NumberFormat.getIntegerInstance(PT_BR)

/**
 * Estado interno guarda apenas dígitos representando centavos.
 * Ex.: "12345" → exibido como "123,45". Cursor sempre no fim,
 * padrão de máscara monetária brasileira. O sufixo "R$" fica
 * a cargo do prefix do campo.
 */
class MoneyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val formatted = formatDigitsAsBrl(digits)
        val mapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = formatted.length
            override fun transformedToOriginal(offset: Int) = digits.length
        }
        return TransformedText(AnnotatedString(formatted), mapping)
    }
}

private fun formatDigitsAsBrl(digits: String): String {
    if (digits.isEmpty()) return ""
    val cents = digits.toLongOrNull() ?: 0L
    val reais = cents / 100
    val centavos = (cents % 100).toString().padStart(2, '0')
    return "${INTEGER_FORMATTER.format(reais)},$centavos"
}

fun digitsToCents(text: String): Long =
    text.filter { it.isDigit() }.toLongOrNull() ?: 0L

fun centsToDigits(cents: Long): String =
    if (cents == 0L) "" else cents.toString()
