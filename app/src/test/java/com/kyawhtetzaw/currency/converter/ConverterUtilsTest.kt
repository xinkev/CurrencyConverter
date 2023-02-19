package com.kyawhtetzaw.currency.converter

import junit.framework.TestCase.assertEquals
import org.junit.Test

internal class ConverterUtilsKtTest {
    val base = "USD"

    @Test
    fun `converting same currency returns original amount`() {
        val amount = 100.0
        val from = "USD"
        val to = "USD"
        val fromRate = 1.0
        val toRate = 1.0

        val result = convertRate(amount, fromRate, toRate, from, to, base)

        val expected = CurrencyConversionResult(
            symbol = to,
            rate = 1.0,
            amount = amount
        )

        assertEquals(expected, result)
    }

    @Test
    fun `converts amount correctly when converting from base currency to another currency`() {        // given
        val amount = 100.0
        val from = "USD"
        val to = "EUR"
        val fromRate = 1.0
        val toRate = 0.85

        val result = convertRate(amount, fromRate, toRate, from, to, base)

        val expect = CurrencyConversionResult(
            symbol = to,
            rate = toRate,
            amount = toRate * amount
        )
        assertEquals(expect, result)
    }

    @Test
    fun `converts amount correctly when converting to base currency from another currency`() {
        val amount = 100.0
        val from = "EUR"
        val to = "USD"
        val fromRate = 1.18
        val toRate = 1.0

        val result = convertRate(amount, fromRate, toRate, from, to, base)

        val expect = CurrencyConversionResult(
            symbol = to,
            rate = toRate,
            amount = toRate * amount
        )
        assertEquals(expect, result)
    }

    @Test
    fun `converts amount correctly when converting between two non-base currencies`() {
        val amount = 100.0
        val from = "GBP"
        val to = "CAD"
        val fromRate = 1.39
        val toRate = 1.25

        val result = convertRate(amount, fromRate, toRate, from, to, base)

        val expect = CurrencyConversionResult(
            symbol = to,
            rate = toRate / fromRate,
            amount = (toRate / fromRate) * amount
        )
        assertEquals(expect, result)
    }
}