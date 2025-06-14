package com.thomaskioko.tvmaniac.search.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_clear_text
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.search.presenter.ClearQuery
import com.thomaskioko.tvmaniac.search.presenter.QueryChanged
import com.thomaskioko.tvmaniac.search.presenter.SearchShowAction
import kotlinx.coroutines.launch

@Composable
fun SearchTextContainer(
    query: String,
    hint: String,
    lazyListState: LazyListState,
    onAction: (SearchShowAction) -> Unit,
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
        hasFocus = hasFocus.value,
        keyboardType = keyboardType,
        onTextChanged = { newValue ->
            textState.value = newValue
            onAction(QueryChanged(newValue.text))
        },
        onFocusChanged = { hasFocus.value = it },
        onClearClick = {
            textState.value = TextFieldValue()
            onAction(ClearQuery)
            keyboardController?.hide()
            focusManager.clearFocus()
        },
        onSubmit = {
            coroutineScope.launch {
                onAction(QueryChanged(textState.value.text))
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
    hasFocus: Boolean,
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
            hasFocus = hasFocus,
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
    hasFocus: Boolean,
    onFocusChanged: (Boolean) -> Unit,
    textFieldValue: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
    hint: String,
    keyboardType: KeyboardType,
    onSubmit: () -> Unit,
    onClearClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(bottom = 8.dp, top = 8.dp),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = if (hasFocus) 3.dp else 1.dp,
        border = BorderStroke(
            width = 1.dp,
            color = if (hasFocus) {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            },
        ),
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
            colors = outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                unfocusedBorderColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.secondary,
            ),
        )
    }
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
                onAction = {},
                content = {},
            )
        }
    }
}
