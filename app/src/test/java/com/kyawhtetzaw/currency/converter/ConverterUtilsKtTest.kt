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
            targetRate = 56.0,
            base = base
        )
        val expected = 100.0
        assertEquals(expected, result)
    }

    @Test
    fun convertRate_differentCurrencies_butTargetNotSameWithBase() {
        val source = "SGD"
        val target = "JPY"
        val base = "USD"
        val amount = 100.0
        val sourceRate = 1.338195
        val targetRate = 134.35083

        val result1 = convertRate(
            amount = amount,
            fromRate = sourceRate,
            from = source,
            to = target,
            targetRate = targetRate,
            base = base
        )

        assertEquals(10_039.70522, result1, 0.1)

    }

    @Test
    fun convertRate_differentCurrencies_butTargetSameWithBase() {
        val source = "USD"
        val target = "JPY"
        val base = "USD"
        val amount = 100.0
        val sourceRate = 1.0
        val targetRate = 133.95255

        val result2 = convertRate(
            amount = amount,
            fromRate = sourceRate,
            from = source,
            to = target,
            targetRate = targetRate,
            base = base
        )
        assertEquals(13_395.255, result2,0.1)

    }
}