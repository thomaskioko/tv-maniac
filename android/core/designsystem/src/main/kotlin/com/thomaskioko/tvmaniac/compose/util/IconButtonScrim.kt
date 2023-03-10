package com.thomaskioko.tvmaniac.compose.util

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Shape

fun Modifier.iconButtonBackgroundScrim(
    enabled: Boolean = true,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float = 0.4f,
    shape: Shape = CircleShape,
): Modifier = composed {
    if (enabled) {
        Modifier.background(
            color = MaterialTheme.colors.surface.copy(alpha = alpha),
            shape = shape,
        )
    } else this
}
