package com.kyawhtetzaw.currency.converter

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.kyawhtetzaw.currency.R
import com.kyawhtetzaw.currency.model.ExchangeRate
import com.kyawhtetzaw.currency.ui.theme.KyawHtetZawTheme
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

internal class CurrencyChooserTest {
    private var selectedCurrency: String? by mutableStateOf(null)
    private val currencies = listOf(
        ExchangeRate("USD", 1.0),
        ExchangeRate("EUR", 0.84),
        ExchangeRate("GBP", 0.72),
        ExchangeRate("JPY", 108.76),
    )

    @get:Rule
    val cr = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        selectedCurrency = null
        cr.setContent {
            KyawHtetZawTheme {
                CurrencyChooser(currencies, selectedCurrency) { currency ->
                    selectedCurrency = currency.symbol
                }
            }
        }
    }

    @Test
    fun dialogDisplayWhenClickSelectButtonIsTapped() {
        cr.onNodeWithTag("selectCurrencyButton")
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        cr.onAllNodes(isDialog())
            .assertCountEquals(1)

        cr.onNodeWithText(cr.activity.getString(R.string.choose_a_currency))
            .assertIsDisplayed()

        cr.onNodeWithText(cr.activity.getString(R.string.choose_a_currency))
            .assertIsDisplayed()
    }

    @Test
    fun availableCurrenciesAreDisplayedInTheDialog() {
        cr.onNodeWithTag("selectCurrencyButton")
            .performClick()

        currencies.forEach { currency ->
            cr.onNodeWithText(currency.symbol)
                .assertIsDisplayed()
                .assertHasClickAction()
        }
    }

    @Test
    fun userCanSelectACurrencyFromDialog() {
        val currency = "EUR"
        cr.onNodeWithTag("selectCurrencyButton")
            .performClick()
        cr.onNodeWithText(currency).performClick()

        assertEquals(currency, selectedCurrency)
    }

    @Test
    fun selectingACurrencyDismissDialog() {
        cr.onNodeWithTag("selectCurrencyButton")
            .performClick()

        cr.onAllNodes(isDialog())
            .assertCountEquals(1)

        cr.onNodeWithText("EUR").performClick()

        cr.onAllNodes(isDialog())
            .assertCountEquals(0)
    }

    @Test
    fun selectCurrencyButtonLabelShouldChangeToSelectedCurrency() {
        val button = cr.onNodeWithTag("selectCurrencyButton")
            .performClick()
        val currency = "EUR"

        cr.onNodeWithText(currency).performClick()
        button.assert(hasText(currency))
    }

}