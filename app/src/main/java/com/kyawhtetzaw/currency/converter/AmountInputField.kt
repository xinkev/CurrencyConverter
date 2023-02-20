package com.kyawhtetzaw.currency.converter

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.kyawhtetzaw.currency.R.string
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(FlowPreview::class, ExperimentalComposeUiApi::class)
@Composable
fun AmountInputField(
    modifier: Modifier = Modifier, initialValue: String?, onValueChange: (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var inputValue by remember { mutableStateOf(initialValue.orEmpty()) }
    val contentDescription = stringResource(string.amount_input_content_description)

    LaunchedEffect(inputValue) {
        snapshotFlow { inputValue }.distinctUntilChanged().debounce(500)
            .collectLatest(onValueChange)
    }

    OutlinedTextField(
        modifier = modifier
            .then(Modifier.semantics { this.contentDescription = contentDescription }),
        value = inputValue,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue.toDoubleOrNull() != null) {
                inputValue = newValue
            }
        },
        placeholder = { Text(text = stringResource(string.amount_input_placeholder)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }
        )
    )
}