package com.thomaskioko.tvmaniac.compose.extensions

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
public fun contentBackgroundGradient(): Brush {
    return Brush.verticalGradient(
        listOf(
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            MaterialTheme.colorScheme.background.copy(alpha = 0.6F),
            MaterialTheme.colorScheme.background.copy(alpha = 0.8F),
            MaterialTheme.colorScheme.background,
        ),
    )
}

@Composable
public fun backgroundGradient(): List<Color> {
    return listOf(
        MaterialTheme.colorScheme.background,
        MaterialTheme.colorScheme.background.copy(alpha = 0.9F),
        MaterialTheme.colorScheme.background.copy(alpha = 0.8F),
        MaterialTheme.colorScheme.background.copy(alpha = 0.7F),
        Color.Transparent,
    )
}
