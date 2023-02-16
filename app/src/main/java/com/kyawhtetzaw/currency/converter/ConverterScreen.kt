package com.kyawhtetzaw.currency.converter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kyawhtetzaw.currency.model.ExchangeRate
import com.kyawhtetzaw.currency.ui.theme.KyawHtetZawTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.time.Duration
import kotlin.concurrent.timer

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = viewModel()
) {
    val exchangeRates by viewModel.convertedRates.collectAsStateWithLifecycle()
    val timer by viewModel.timer.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        timer(period = Duration.ofMinutes(30).toMillis()) {
            viewModel.updateExchangeRates()
        }
    }

    ConverterScreenLayout(
        timer = timer,
        exchangeRates = exchangeRates,
        onAmountValueChange = viewModel::setInputAmount
    )
}

@Composable
fun ConverterScreenLayout(
    timer: String?,
    exchangeRates: List<ExchangeRate>,
    onAmountValueChange: (String) -> Unit,
) {

    Column(Modifier.padding(16.dp)) {
        AnimatedVisibility(visible = timer != null) {
            Box(Modifier.fillMaxWidth()) {
                timer?.let {
                    Text(
                        text = "Updating exchange rates in $it",
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
            }
        }

        InputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            initialValue = "",
            onValueChange = onAmountValueChange
        )

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

@OptIn(FlowPreview::class)
@Composable
fun InputField(
    modifier: Modifier = Modifier,
    initialValue: String?,
    onValueChange: (String) -> Unit
) {

    var inputValue by remember { mutableStateOf(initialValue.orEmpty()) }

    LaunchedEffect(inputValue) {
        snapshotFlow { inputValue }
            .distinctUntilChanged()
            .debounce(500)
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
        placeholder = { Text(text = "Amount") }
    )
}

@Preview(showBackground = true)
@Composable
fun ConverterScreenPreview() {
    KyawHtetZawTheme {
        ConverterScreenLayout(
            timer = "",
            exchangeRates = emptyList(),
            onAmountValueChange = {}
        )
    }
}
