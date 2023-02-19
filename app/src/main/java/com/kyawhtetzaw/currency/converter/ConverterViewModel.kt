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

    val exchangeRates = getExchangeRates().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        emptyList()
    )

    private val _selectedExchangeRate: MutableStateFlow<ExchangeRate?> = MutableStateFlow(null)
    val selectedCurrency = _selectedExchangeRate.asStateFlow()

    private val _exchangeRateUpdateState =
        MutableStateFlow<ExchangeRateUpdateState>(ExchangeRateUpdateState.Updating)
    val exchangeRateUpdateState = _exchangeRateUpdateState.asStateFlow()

    val convertedRates =
        combine(inputAmount, exchangeRates, selectedCurrency) { amount, rates, source ->
            if (rates.isNotEmpty() && source != null) {
                rates.map { to ->
                    convertRate(
                        amount = amount,
                        fromRate = source.rate,
                        targetRate = to.rate,
                        from = source.symbol,
                        to = to.symbol,
                        base = Config.BaseCurrency
                    )
                }
            } else emptyList()
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(500),
            emptyList()
        )

    init {
        startExchangeRateUpdater()
    }

    fun startExchangeRateUpdater() {
        viewModelScope.launch {
            exchangeRateUpdater.start(Config.WaitInterval)
                .collect { newState ->
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
        _selectedExchangeRate.update { value }
    }
}