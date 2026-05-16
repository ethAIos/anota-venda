package com.caderninho.vendas.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val DAY_MONTH = DateTimeFormatter.ofPattern("dd/MM", Locale("pt", "BR"))
private val SHORT_DAY = DateTimeFormatter.ofPattern("EEE dd/MM", Locale("pt", "BR"))
private val FULL_DAY = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("pt", "BR"))

fun formatDayMonth(date: LocalDate): String = date.format(DAY_MONTH)

fun formatShortDay(date: LocalDate): String =
    date.format(SHORT_DAY).replaceFirstChar { it.lowercase() }

fun formatFullDay(date: LocalDate): String =
    date.format(FULL_DAY).replaceFirstChar { it.lowercase() }

fun describeDue(due: LocalDate, today: LocalDate = LocalDate.now()): String {
    val days = ChronoUnit.DAYS.between(today, due)
    return when {
        days < 0L -> "atrasado ${-days} dia${if (days == -1L) "" else "s"}"
        days == 0L -> "vence hoje"
        days == 1L -> "vence amanhã"
        days in 2L..6L -> "vence ${formatShortDay(due).substringBefore(' ')}"
        else -> "vence ${formatDayMonth(due)}"
    }
}
