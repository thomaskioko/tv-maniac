package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

private const val INDICATOR_ALPHA = 0.7f
private const val UNFOCUSED_INDICATOR_ALPHA = 0.3f
private const val PLACEHOLDER_ALPHA = 0.6f

@Composable
public fun tvManiacTextFieldColors(): TextFieldColors = TextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = PLACEHOLDER_ALPHA),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = PLACEHOLDER_ALPHA),
    focusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = INDICATOR_ALPHA),
    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = UNFOCUSED_INDICATOR_ALPHA),
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    cursorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = INDICATOR_ALPHA),
)
