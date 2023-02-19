package com.kyawhtetzaw.currency.converter

import junit.framework.TestCase.assertEquals
import org.junit.Test

internal class ConverterUtilsKtTest {

    @Test
    fun convertRate_sameCurrency_returnsSameAmount() {
        val from = "JPY"
        val to = "JPY"
        val base = "USD"
        val result = convertRate(
            amount = 100.0,
            fromRate = 120.0,
            from = from,
            to = to,
            toRate = 56.0,
            base = base
        )
        val expected = CurrencyConversionResult(
            symbol = "JPY",
            rate = 1.0,
            amount = 100.0
        )
        assertEquals(expected, result)
    }

    @Test
    fun convertRate_differentCurrencies_butToCurrencyIsNotSameWithBase() {
        val result = convertRate(
            amount = 100.0,
            fromRate =  1.338195,
            from = "SGD",
            to = "JPY",
            toRate = 134.35083,
            base = "USD"
        )
        
        val expected = CurrencyConversionResult(
            symbol = "JPY",
            rate = 100.3970497573224,
            amount = 10039.70522
        )

        assertEquals(expected.symbol, result.symbol)
        assertEquals(expected.rate, result.rate, 0.1)
        assertEquals(expected.amount, result.amount, 0.1)
    }

    @Test
    fun convertRate_differentCurrencies_butToSameWithBase() {
        val result = convertRate(
            amount = 100.0,
            fromRate = 1.0,
            from = "USD",
            to = "JPY",
            toRate = 133.95255,
            base = "USD"
        )

        val expected = CurrencyConversionResult(
            symbol = "JPY",
            rate = 133.95255,
            amount = 13395.255
        )

        assertEquals(expected.symbol, result.symbol)
        assertEquals(expected.rate, result.rate, 0.1)
        assertEquals(expected.amount, result.amount, 0.1)
    }
}