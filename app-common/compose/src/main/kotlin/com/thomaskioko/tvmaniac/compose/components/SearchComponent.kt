package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    hint: String,
    textState: MutableState<TextFieldValue>,
    onValueChange: (String) -> Unit
) {
    var textFieldFocusState by remember { mutableStateOf(false) }

    SearchInputText(
        textFieldValue = textState.value,
        onTextChanged = {
            textState.value = it
            onValueChange.invoke(it.text)
        },
        keyboardShown = textFieldFocusState,
        onTextFieldFocused = { focused ->
            textFieldFocusState = focused
        },
        hint = hint,
        focusState = textFieldFocusState
    )
}

val KeyboardShownKey = SemanticsPropertyKey<Boolean>("KeyboardShownKey")
var SemanticsPropertyReceiver.keyboardShownProperty by KeyboardShownKey

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchInputText(
    keyboardType: KeyboardType = KeyboardType.Text,
    onTextChanged: (TextFieldValue) -> Unit,
    textFieldValue: TextFieldValue,
    keyboardShown: Boolean,
    hint: String = "",
    onTextFieldFocused: (Boolean) -> Unit,
    focusState: Boolean
) {
    var keyboardController by remember { mutableStateOf<SoftwareKeyboardController?>(null) }

    LaunchedEffect(keyboardController, keyboardShown) {
        keyboardController?.let {
            if (keyboardShown) it.show() else it.hide()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .semantics { keyboardShownProperty = keyboardShown },
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .height(45.dp)
                .weight(1f)
                .align(Alignment.Bottom)
        ) {
            var lastFocusState by remember { mutableStateOf(Recomposer.State.Inactive) }

            BasicTextField(
                value = textFieldValue,
                onValueChange = { onTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
                    .align(Alignment.CenterStart)
                    .onFocusEvent { state ->
                        //TODO:: Handle focus state
                    },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search,
                    keyboardType = keyboardType
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // TODO:: Invoke Search Action
                    }
                ),
                textStyle = MaterialTheme.typography.body2,
            )

            if (textFieldValue.text.isEmpty() && !focusState) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp),
                    text = hint,
                    style = MaterialTheme.typography.body2.copy(MaterialTheme.colors.onSurface)
                )
            }
        }
    }
}
