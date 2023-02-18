package com.kyawhtetzaw.currency.converter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
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
import com.kyawhtetzaw.currency.ExchangeRateUpdateState
import com.kyawhtetzaw.currency.model.ExchangeRate
import com.kyawhtetzaw.currency.ui.icons.SuccessIcon
import com.kyawhtetzaw.currency.ui.theme.KyawHtetZawTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = viewModel()
) {
    val exchangeRates by viewModel.convertedRates.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val exchangeRateUpdateState by viewModel.exchangeRateUpdateState.collectAsStateWithLifecycle()

    ConverterScreenLayout(
        exchangeRateUpdateState = exchangeRateUpdateState,
        selectedCurrency = selectedCurrency?.symbol,
        exchangeRates = exchangeRates,
        onAmountValueChange = viewModel::setInputAmount,
        onCurrencySelected = viewModel::setSelectedCurrency,
        onRetryClick = viewModel::startExchangeRateUpdater
    )
}

@Composable
fun ConverterScreenLayout(
    exchangeRateUpdateState: ExchangeRateUpdateState,
    selectedCurrency: String?,
    exchangeRates: List<ExchangeRate>,
    onAmountValueChange: (String) -> Unit,
    onCurrencySelected: (ExchangeRate) -> Unit,
    onRetryClick: () -> Unit
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
        when (exchangeRateUpdateState) {
            is ExchangeRateUpdateState.Error -> Text(
                modifier = Modifier.clickable(onClick = onRetryClick),
                text = "Update failed. Try Again.",
                color = MaterialTheme.colors.primary
            )
            is ExchangeRateUpdateState.Success -> Icon(
                imageVector = SuccessIcon,
                contentDescription = "Success",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(20.dp)
            )
            is ExchangeRateUpdateState.Timer -> Text(
                text = "Updating exchange rates in ${exchangeRateUpdateState.time}"
            )
            is ExchangeRateUpdateState.Updating -> CircularProgressIndicator(Modifier.size(16.dp))
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
            selectedCurrency = null,
            exchangeRates = emptyList(),
            onAmountValueChange = {},
            onCurrencySelected = {},
            exchangeRateUpdateState = ExchangeRateUpdateState.Updating,
            onRetryClick = {}
        )
    }
}
