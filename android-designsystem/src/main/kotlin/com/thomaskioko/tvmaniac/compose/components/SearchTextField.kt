package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_clear_text
import com.thomaskioko.tvmaniac.i18n.resolve
import kotlinx.coroutines.launch

@Composable
public fun SearchTextContainer(
    query: String,
    hint: String,
    lazyListState: LazyListState,
    onQueryChanged: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    content: @Composable () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    val textState = remember(query) {
        mutableStateOf(TextFieldValue(query, TextRange(query.length)))
    }
    val hasFocus = remember { mutableStateOf(false) }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            }
    }

    SearchTextFieldContent(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                )
            },
        textFieldValue = textState.value,
        hint = hint,
        keyboardType = keyboardType,
        onTextChanged = { newValue ->
            textState.value = newValue
            onQueryChanged(newValue.text)
        },
        onFocusChanged = { hasFocus.value = it },
        onClearClick = {
            textState.value = TextFieldValue()
            onClearQuery()
            keyboardController?.hide()
            focusManager.clearFocus()
        },
        onSubmit = {
            coroutineScope.launch {
                onQueryChanged(textState.value.text)
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        },
        content = content,
    )
}

@Composable
private fun SearchTextFieldContent(
    textFieldValue: TextFieldValue,
    hint: String,
    keyboardType: KeyboardType,
    onTextChanged: (TextFieldValue) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onClearClick: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        SearchTextField(
            onFocusChanged = onFocusChanged,
            textFieldValue = textFieldValue,
            onTextChanged = onTextChanged,
            hint = hint,
            keyboardType = keyboardType,
            onSubmit = onSubmit,
            onClearClick = onClearClick,
        )

        content()
    }
}

@Composable
private fun SearchTextField(
    onFocusChanged: (Boolean) -> Unit,
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    hint: String,
    keyboardType: KeyboardType,
    onSubmit: () -> Unit,
    onClearClick: () -> Unit,
    shape: Shape = MaterialTheme.shapes.medium,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { onFocusChanged(it.isFocused) },
        value = textFieldValue,
        onValueChange = onTextChanged,
        placeholder = {
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                ),
            )
        },
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSubmit() },
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        },
        trailingIcon = {
            IconButton(onClick = onClearClick) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = cd_clear_text.resolve(LocalContext.current),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        },
        shape = shape,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            cursorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        ),
    )
}

@ThemePreviews
@Composable
private fun SearchTextFieldPreview() {
    TvManiacTheme {
        Surface {
            SearchTextContainer(
                hint = "Enter Show Title",
                query = "",
                lazyListState = remember { LazyListState() },
                onClearQuery = {},
                onQueryChanged = {},
                content = {},
            )
        }
    }
}
