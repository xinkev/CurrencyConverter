package com.kyawhtetzaw.currency.converter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import timber.log.Timber

@Composable
fun ConversionResults(data: List<CurrencyConversionResult>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(items = data, key = { it.symbol }) {
            ListItem(result = it)
        }
    }
}

@Composable
private fun ListItem(result: CurrencyConversionResult) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(text = result.symbol)
            Text(text = "Rate " + result.rate, style = MaterialTheme.typography.caption)
        }

        Text(
            text = result.amount.toString(),
        )
    }
}

@Preview
@Composable
fun ExchangeRateListItemPreview() {
    ListItem(CurrencyConversionResult("USD", 100.0, 1.0))
}
