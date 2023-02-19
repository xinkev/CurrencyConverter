package com.kyawhtetzaw.currency.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kyawhtetzaw.currency.R
import com.kyawhtetzaw.currency.model.ExchangeRate

@Composable
fun CurrencyChooser(
    currencies: List<ExchangeRate>,
    selectedCurrency: String?,
    onSelect: (ExchangeRate) -> Unit
) {
    var showSelector by remember { mutableStateOf(false) }

    CurrencyChooserDialog(
        show = showSelector,
        currencies = currencies,
        onDismissRequest = { showSelector = false },
        onSelect = onSelect
    )

    Row(modifier = Modifier.clickable(onClick = { showSelector = true })) {
        Text(
            text = selectedCurrency ?: stringResource(R.string.lbl_select_currency),
            color = MaterialTheme.colors.primary
        )
        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            tint = MaterialTheme.colors.primary
        )
    }
}
@Composable
private fun CurrencyChooserDialog(
    show: Boolean,
    currencies: List<ExchangeRate>,
    onDismissRequest: () -> Unit,
    onSelect: (ExchangeRate) -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = onDismissRequest) {
            CurrencyChooserDialogLayout(
                currencies = currencies,
                onSelect = {
                    onSelect(it)
                    onDismissRequest()
                }
            )
        }
    }
}

@Composable
private fun CurrencyChooserDialogLayout(
    currencies: List<ExchangeRate>,
    onSelect: (ExchangeRate) -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(
                items = currencies,
                key = { _, currency -> currency.symbol }) { i, currency ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = { onSelect(currency) }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currency.symbol,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                if (i != currencies.lastIndex) {
                    Divider()
                }
            }
        }
    }
}

@Preview
@Composable
private fun CurrencyChooserPreview() {
    val currencies = listOf(ExchangeRate("USD", 1.0), ExchangeRate("HKD", 11.0))

    CurrencyChooserDialogLayout(currencies = currencies, onSelect = {})
}