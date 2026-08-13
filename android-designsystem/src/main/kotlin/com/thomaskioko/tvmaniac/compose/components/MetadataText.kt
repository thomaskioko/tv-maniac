package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
public fun metadataWithAccentDots(parts: List<String>): AnnotatedString {
    val dividerStyle = SpanStyle(color = MaterialTheme.colorScheme.secondary)
    return buildAnnotatedString {
        parts.forEachIndexed { index, part ->
            if (index > 0) {
                withStyle(dividerStyle) { append(" · ") }
            }
            append(part)
        }
    }
}
