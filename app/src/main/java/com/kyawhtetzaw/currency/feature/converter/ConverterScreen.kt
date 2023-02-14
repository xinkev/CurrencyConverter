package com.kyawhtetzaw.currency.feature.converter

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyawhtetzaw.currency.model.ExchangeRate


@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = viewModel()
) {
    val list = remember { mutableStateListOf<ExchangeRate>() }
    LaunchedEffect(Unit) {
        val a = viewModel.getExchangeRates()
        list.addAll(a)
    }

    LazyColumn {
        items(items = list) {
            Text(text = it.symbol)
        }
    }
}