package com.kyawhtetzaw.currency.feature.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSource
import com.kyawhtetzaw.currency.usecase.GetExchangeRates
import com.kyawhtetzaw.currency.usecase.GetLastUpdatedTime
import com.kyawhtetzaw.currency.usecase.UpdateExchangeRates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val updateExchangeRates: UpdateExchangeRates,
    private val getLastUpdatedTime: GetLastUpdatedTime,
    private val lastUpdateDataSource: LastUpdateDataSource,
    getExchangeRates: GetExchangeRates,
) : ViewModel() {
    private val inputAmount = MutableStateFlow(0.0)

    private val exchangeRates = getExchangeRates().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        emptyList()
    )

    val convertedRates = inputAmount.combine(exchangeRates) { amount, rates ->
        rates.map { it.copy(rate = it.rate * amount.toDouble()) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        exchangeRates.value
    )
    private val _timer = MutableStateFlow<String?>(null)
    val timer = _timer.asStateFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        // Get last updated time
        viewModelScope.launch {
            val timeLimit = 1.minutes.inWholeSeconds
            var timePassed = getLastUpdatedTime()?.let {
                ChronoUnit.SECONDS.between(it, LocalDateTime.now())
            }

            // start the counter
            while (true) {
                // compares the time limit and the time passed
                if (timePassed == null || timePassed > timeLimit) {
                    lastUpdateDataSource.saveTimestamp(LocalDateTime.now())
                    timePassed = 0
                    // and update exchange rates if time limit is reached
                    continue
                } else {
                    updateCountDown(timeLimit - timePassed)
                    timePassed += 1L
                    delay(1.seconds)
                }
            }
        }
    }

    private fun updateCountDown(time: Long) {
        val min = time / 60
        val sec = time % 60
        _timer.update { ("%02d:%02d".format(min, sec)) }
    }

    fun updateExchangeRates() {
        viewModelScope.launch {
            updateExchangeRates.invoke()
        }
    }

    fun setInputAmount(value: String) {
        inputAmount.update { oldValue ->
            if (value.isEmpty()) {
                0.0
            } else {
                value.toDoubleOrNull()?:oldValue
            }
        }
    }
}