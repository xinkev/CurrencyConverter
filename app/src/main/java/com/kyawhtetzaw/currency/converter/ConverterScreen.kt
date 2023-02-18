package com.kyawhtetzaw.currency.converter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyawhtetzaw.currency.model.ExchangeRate
import com.kyawhtetzaw.currency.ui.theme.KyawHtetZawTheme
import java.time.Duration
import kotlin.concurrent.timer
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = viewModel()
) {
    val exchangeRates by viewModel.convertedRates.collectAsStateWithLifecycle()
    val timer by viewModel.timer.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        timer(period = Duration.ofMinutes(30).toMillis()) {
            viewModel.updateExchangeRates()
        }
    }

    ConverterScreenLayout(
        timer = timer,
        selectedCurrency = selectedCurrency?.symbol,
        exchangeRates = exchangeRates,
        onAmountValueChange = viewModel::setInputAmount,
        onCurrencySelected = viewModel::setSelectedCurrency
    )
}

@Composable
fun ConverterScreenLayout(
    timer: String?,
    selectedCurrency: String?,
    exchangeRates: List<ExchangeRate>,
    onAmountValueChange: (String) -> Unit,
    onCurrencySelected: (ExchangeRate) -> Unit
) {
    var showSelector by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        Row(modifier = Modifier.clickable(onClick = { showSelector = true })) {
            Text(
                text = selectedCurrency ?: "Select a currency",
                color = MaterialTheme.colors.primary
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colors.primary
            )
        }

        InputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            initialValue = "",
            onValueChange = onAmountValueChange
        )

        AnimatedVisibility(visible = timer != null) {
            timer?.let { Text(text = "Updating exchange rates in $it") }
        }

        AnimatedVisibility(
            visible = selectedCurrency != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp)
            ) {
                items(items = exchangeRates, key = { it.symbol }) {
                    Text(text = "${it.symbol} ${it.rate}")
                }
            }
        }
    }

    CurrencySelectorDialog(
        show = showSelector,
        currencies = exchangeRates,
        onDismissRequest = { showSelector = false },
        onSelect = onCurrencySelected
    )
}

@OptIn(FlowPreview::class)
@Composable
fun InputField(
    modifier: Modifier = Modifier, initialValue: String?, onValueChange: (String) -> Unit
) {
    var inputValue by remember { mutableStateOf(initialValue.orEmpty()) }

    LaunchedEffect(inputValue) {
        snapshotFlow { inputValue }.distinctUntilChanged().debounce(500)
            .collectLatest(onValueChange)
    }

    OutlinedTextField(modifier = modifier, value = inputValue, onValueChange = { newValue ->
        if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
            inputValue = newValue
        }
    }, placeholder = { Text(text = "Amount") })
}

@Preview(showBackground = true)
@Composable
fun ConverterScreenPreview() {
    KyawHtetZawTheme {
        ConverterScreenLayout(
            timer = "",
            exchangeRates = emptyList(),
            onAmountValueChange = {},
            selectedCurrency = null,
            onCurrencySelected = {}
        )
    }
}
