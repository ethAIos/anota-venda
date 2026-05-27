package com.caderninho.vendas.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Test

class DatePickerDatesTest {
    @Test
    fun selectedDateMillisRoundTripsAsSameCivilDateInSaoPaulo() {
        val previousDefault = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"))

            val selectedDate = LocalDate.of(2026, 6, 15)
            val selectedMillis = selectedDate.toDatePickerMillis()

            assertEquals(selectedDate, selectedMillis.toDatePickerLocalDate())
        } finally {
            TimeZone.setDefault(previousDefault)
        }
    }

    @Test
    fun datePickerMillisUsesUtcMidnightContract() {
        val selectedDate = LocalDate.of(2026, 6, 15)

        assertEquals(
            Instant.parse("2026-06-15T00:00:00Z").toEpochMilli(),
            selectedDate.toDatePickerMillis(),
        )
        assertEquals(
            selectedDate,
            Instant.parse("2026-06-15T00:00:00Z").toEpochMilli().toDatePickerLocalDate(),
        )
        assertEquals(
            selectedDate,
            Instant.ofEpochMilli(selectedDate.toDatePickerMillis()).atZone(ZoneOffset.UTC).toLocalDate(),
        )
    }
}
