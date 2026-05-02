package com.thomaskioko.tvmaniac.compose.extensions

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
public fun contentBackgroundGradient(): Brush {
    val background = MaterialTheme.colorScheme.background
    return remember(background) {
        Brush.verticalGradient(
            listOf(
                Color.Transparent,
                Color.Transparent,
                Color.Transparent,
                background.copy(alpha = 0.6F),
                background.copy(alpha = 0.8F),
                background,
            ),
        )
    }
}

@Composable
public fun backgroundGradient(): List<Color> {
    val background = MaterialTheme.colorScheme.background
    return remember(background) {
        listOf(
            background,
            background.copy(alpha = 0.9F),
            background.copy(alpha = 0.8F),
            background.copy(alpha = 0.7F),
            Color.Transparent,
        )
    }
}
