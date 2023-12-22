package com.thomaskioko.tvmaniac.compose.extensions

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun contentBackgroundGradient(): Brush {
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
