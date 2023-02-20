package com.kyawhtetzaw.currency.converter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kyawhtetzaw.currency.ExchangeRateUpdateState
import com.kyawhtetzaw.currency.R
import com.kyawhtetzaw.currency.ui.icons.SuccessIcon

@Composable
fun UpdateTimerView(
    modifier: Modifier = Modifier,
    exchangeRateUpdateState: State<ExchangeRateUpdateState>,
    onRetryClick: () -> Unit
) {
    Box(modifier = modifier) {
        when (val state = exchangeRateUpdateState.value) {
            is ExchangeRateUpdateState.Error -> ErrorView(onRetryClick)
            is ExchangeRateUpdateState.Success -> SuccessView()
            is ExchangeRateUpdateState.Timer -> CountDownView(state)
            is ExchangeRateUpdateState.Updating -> CircularProgressIndicator(Modifier.size(16.dp))
        }
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
        contentDescription = stringResource(R.string.success_icon_content_description),
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
