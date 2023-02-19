package com.kyawhtetzaw.currency.converter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyawhtetzaw.currency.ExchangeRateUpdateState
import com.kyawhtetzaw.currency.R
import com.kyawhtetzaw.currency.model.ExchangeRate
import com.kyawhtetzaw.currency.ui.icons.SuccessIcon
import com.kyawhtetzaw.currency.ui.theme.KyawHtetZawTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = viewModel()
) {
    val conversionResults by viewModel.convertedRates.collectAsStateWithLifecycle()
    val selectedCurrency by viewModel.selectedCurrency.collectAsStateWithLifecycle()
    val exchangeRateUpdateState = viewModel.exchangeRateUpdateState.collectAsStateWithLifecycle()
    val exchangeRates by viewModel.exchangeRates.collectAsStateWithLifecycle()

    ConverterScreenLayout(
        exchangeRateUpdateState = exchangeRateUpdateState,
        selectedCurrency = selectedCurrency?.symbol,
        conversionResults = conversionResults,
        exchangeRates = exchangeRates,
        onAmountValueChange = viewModel::setInputAmount,
        onCurrencySelect = viewModel::setSelectedCurrency,
        onRetryClick = viewModel::startExchangeRateUpdater
    )
}

@Composable
fun ConverterScreenLayout(
    exchangeRates: List<ExchangeRate>,
    exchangeRateUpdateState: State<ExchangeRateUpdateState>,
    selectedCurrency: String?,
    conversionResults: List<CurrencyConversionResult>,
    onAmountValueChange: (String) -> Unit,
    onCurrencySelect: (ExchangeRate) -> Unit,
    onRetryClick: () -> Unit
) {

    Column {
        Column(Modifier.padding(16.dp)) {
            CurrencyChooser(
                currencies = exchangeRates,
                selectedCurrency = selectedCurrency,
                onSelect = onCurrencySelect
            )

            InputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                initialValue = "",
                onValueChange = onAmountValueChange
            )
            UpdateTimerView(exchangeRateUpdateState, onRetryClick)
        }

        Card(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            ConversionResults(conversionResults)
        }
    }
}

@Composable
private fun UpdateTimerView(
    exchangeRateUpdateState: State<ExchangeRateUpdateState>,
    onRetryClick: () -> Unit
) {
    when (val state = exchangeRateUpdateState.value) {
        is ExchangeRateUpdateState.Error -> ErrorView(onRetryClick)
        is ExchangeRateUpdateState.Success -> SuccessView()
        is ExchangeRateUpdateState.Timer -> CountDownView(state)
        is ExchangeRateUpdateState.Updating -> CircularProgressIndicator(Modifier.size(16.dp))
    }
}

@Composable
private fun CountDownView(exchangeRateUpdateState: ExchangeRateUpdateState.Timer) {
    Text(
        text = stringResource(
            id = R.string.lbl_update_count_down,
            exchangeRateUpdateState.time
        )
    )
}

@Composable
private fun SuccessView() {
    Icon(
        imageVector = SuccessIcon,
        contentDescription = "Success",
        tint = MaterialTheme.colors.primary,
        modifier = Modifier.size(20.dp)
    )
}

@Composable
private fun ErrorView(onRetryClick: () -> Unit) {
    Text(
        modifier = Modifier.clickable(onClick = onRetryClick),
        text = stringResource(R.string.lbl_update_failed),
        color = MaterialTheme.colors.primary
    )
}

@OptIn(FlowPreview::class, ExperimentalComposeUiApi::class)
@Composable
fun InputField(
    modifier: Modifier = Modifier, initialValue: String?, onValueChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var inputValue by remember { mutableStateOf(initialValue.orEmpty()) }

    LaunchedEffect(inputValue) {
        snapshotFlow { inputValue }.distinctUntilChanged().debounce(500)
            .collectLatest(onValueChange)
    }

    OutlinedTextField(
        modifier = modifier,
        value = inputValue,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                inputValue = newValue
            }
        },
        placeholder = { Text(text = "Amount") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ConverterScreenPreview() {
    val currencies = listOf(ExchangeRate("USD", 1.0), ExchangeRate("HKD", 11.0))
    val updateState = remember { mutableStateOf(ExchangeRateUpdateState.Updating) }
    KyawHtetZawTheme {
        ConverterScreenLayout(
            selectedCurrency = "USD",
            conversionResults = emptyList(),
            exchangeRates = currencies,
            onAmountValueChange = {},
            onCurrencySelect = {},
            exchangeRateUpdateState = updateState,
            onRetryClick = {}
        )
    }
}
