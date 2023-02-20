package com.kyawhtetzaw.currency.converter

import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.kyawhtetzaw.currency.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AmountInputFieldTest {
    private lateinit var contentDescription: String
    private var value by mutableStateOf<String?>(null)

    @get:Rule
    val cr = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        cr.setContent {
            contentDescription = stringResource(id = R.string.amount_input_content_description)
            AmountInputField(initialValue = value, onValueChange = { value = it })
        }
    }

    @Test
    fun shouldOnlyAcceptValidDecimalValue() {
        val contentDescription = cr.activity.getString(R.string.amount_input_content_description)
        val placeholder = cr.activity.getString(R.string.amount_input_placeholder)
        val invalidInput1 = "123abcd"
        val invalidInput2 = "1.1.1"
        val validInput1 = "1223"
        val validInput2 = "1.001"
        val inputField = cr.onNodeWithContentDescription(contentDescription)

        inputField.performTextInput(invalidInput1)
        inputField.assertTextEquals(placeholder, "")// takes both Text and EditableText
        inputField.performTextClearance()
        inputField.performTextInput(invalidInput2)
        inputField.assertTextEquals(placeholder, "")
        inputField.performTextClearance()

        inputField.performTextInput(validInput1)
        inputField.assertTextEquals(validInput1)
        inputField.performTextClearance()
        inputField.performTextInput(validInput2)
        inputField.assertTextEquals(validInput2)

        cr.waitUntil { value == validInput2}
    }
}