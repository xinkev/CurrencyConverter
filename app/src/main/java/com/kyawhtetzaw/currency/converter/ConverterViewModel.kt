package com.kyawhtetzaw.currency.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyawhtetzaw.currency.Config
import com.kyawhtetzaw.currency.ExchangeRateUpdateState
import com.kyawhtetzaw.currency.ExchangeRateUpdater
import com.kyawhtetzaw.currency.model.ExchangeRate
import com.kyawhtetzaw.currency.usecase.GetExchangeRates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val exchangeRateUpdater: ExchangeRateUpdater,
    getExchangeRates: GetExchangeRates,
) : ViewModel() {
    private val inputAmount = MutableStateFlow(0.0)

    private val _selectedCurrency = MutableStateFlow<ExchangeRate?>(null)
    val selectedCurrency = _selectedCurrency.asStateFlow()

    private val _exchangeRateUpdateState =
        MutableStateFlow<ExchangeRateUpdateState>(ExchangeRateUpdateState.Updating)
    val exchangeRateUpdateState = _exchangeRateUpdateState.asStateFlow()

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
                            base = Config.BaseCurrency
                        )
                    )
                }
            } else rates
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(500),
            exchangeRates.value
        )

    init {
        startExchangeRateUpdater()
    }

    fun startExchangeRateUpdater() {
        viewModelScope.launch {
            exchangeRateUpdater.start(Config.WaitInterval)
                .collect {newState ->
                    _exchangeRateUpdateState.update { newState }
                }
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