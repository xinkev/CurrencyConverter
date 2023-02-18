package com.kyawhtetzaw.currency.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyawhtetzaw.currency.BaseCurrency
import com.kyawhtetzaw.currency.data.local.preferences.LastUpdateDataSource
import com.kyawhtetzaw.currency.model.ExchangeRate
import com.kyawhtetzaw.currency.usecase.GetExchangeRates
import com.kyawhtetzaw.currency.usecase.GetLastUpdatedTime
import com.kyawhtetzaw.currency.usecase.UpdateExchangeRates
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val updateExchangeRates: UpdateExchangeRates,
    private val getLastUpdatedTime: GetLastUpdatedTime,
    private val lastUpdateDataSource: LastUpdateDataSource,
    getExchangeRates: GetExchangeRates,
) : ViewModel() {
    private val inputAmount = MutableStateFlow(0.0)

    private val _selectedCurrency = MutableStateFlow<ExchangeRate?>(null)
    val selectedCurrency = _selectedCurrency.asStateFlow()

    private val exchangeRates = getExchangeRates().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        emptyList()
    )

    val convertedRates =
        combine(inputAmount, exchangeRates, selectedCurrency) { amount, rates, source ->
            if (rates.isNotEmpty() && source != null) {
                val from = rates.first { it.symbol == source.symbol }
                rates.map { to ->
                    to.copy(
                        rate = convertRate(
                            amount = amount,
                            rate = from.rate,
                            targetRate = to.rate,
                            from = from.symbol,
                            to = to.symbol,
                            base = BaseCurrency
                        )
                    )
                }
            } else rates
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
                value.toDoubleOrNull() ?: oldValue
            }
        }
    }

    fun setSelectedCurrency(value: ExchangeRate) {
        _selectedCurrency.update { value }
    }
}