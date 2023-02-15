package com.kyawhtetzaw.currency.feature.converter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.Duration
import kotlin.concurrent.timer

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = viewModel()
) {
    val exchangeRates by viewModel.exchangeRates.collectAsStateWithLifecycle()
    val timer by viewModel.timer.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        timer(period = Duration.ofMinutes(30).toMillis()) {
            viewModel.updateExchangeRates()
        }
    }

    Column {
        timer?.let { Text(text = it) }

        LazyColumn(Modifier.fillMaxSize()) {
            items(items = exchangeRates, key = { it.symbol }) {
                Text(text = "${it.symbol} ${it.rate}")
            }
        }
    }
}