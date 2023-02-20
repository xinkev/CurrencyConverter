package com.kyawhtetzaw.currency.converter

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.kyawhtetzaw.currency.ExchangeRateUpdateState
import com.kyawhtetzaw.currency.R
import org.junit.Rule
import org.junit.Test

internal class UpdateTimerViewTest {
    @get:Rule
    val cr = createAndroidComposeRule<ComponentActivity>()


    @Test
    fun showUpdatingViewWhenRatesAreBeingUpdate() {
        val state = mutableStateOf(ExchangeRateUpdateState.Updating)
        cr.setContent {
            UpdateTimerView(exchangeRateUpdateState = state, onRetryClick = {})
        }

        cr.onNode(
            hasProgressBarRangeInfo(
                ProgressBarRangeInfo(
                    current = 0.0F,
                    range = 0.0f..0.0f,
                    steps = 0
                )
            )
        )
    }

    @Test
    fun showErrorViewWhenUpdateFailed() {
        val error = "update failed"
        val state = mutableStateOf(ExchangeRateUpdateState.Error(error))
        cr.setContent {
            UpdateTimerView(exchangeRateUpdateState = state, onRetryClick = {})
        }

        cr.onNode(hasText(error))
    }

    @Test
    fun showCountDownTimerViewWhenUpdateFailed() {
        val timer = "10:00"
        val state = mutableStateOf(ExchangeRateUpdateState.Timer(timer))
        cr.setContent {
            UpdateTimerView(exchangeRateUpdateState = state, onRetryClick = {})
        }

        cr.onNode(hasText(cr.activity.getString(R.string.lbl_update_count_down, timer)))
    }

    @Test
    fun showSuccessViewWhenUpdateFailed() {
        val state = mutableStateOf(ExchangeRateUpdateState.Success)
        cr.setContent {
            UpdateTimerView(exchangeRateUpdateState = state, onRetryClick = {})
        }

        cr.onNodeWithContentDescription(cr.activity.getString(R.string.success_icon_content_description))
    }
}