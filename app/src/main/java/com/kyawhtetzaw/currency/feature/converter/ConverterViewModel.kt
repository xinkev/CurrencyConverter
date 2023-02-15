package com.kyawhtetzaw.currency.feature.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyawhtetzaw.currency.usecase.GetExchangeRates
import com.kyawhtetzaw.currency.usecase.UpdateExchangeRates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val getExchangeRates: GetExchangeRates,
    private val updateExchangeRates: UpdateExchangeRates
) : ViewModel() {
    val exchangeRates = getExchangeRates().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(500),
        emptyList()
    )

    fun updateExchangeRates() {
        viewModelScope.launch {
            updateExchangeRates()
        }
    }

}