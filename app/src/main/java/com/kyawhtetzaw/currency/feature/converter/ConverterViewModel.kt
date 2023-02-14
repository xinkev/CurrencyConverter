package com.kyawhtetzaw.currency.feature.converter

import androidx.lifecycle.ViewModel
import com.kyawhtetzaw.currency.usecase.GetExchangeRates
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
     val getExchangeRates: GetExchangeRates
) : ViewModel() {

}