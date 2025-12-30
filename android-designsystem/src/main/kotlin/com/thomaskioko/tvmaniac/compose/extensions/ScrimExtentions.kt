package com.thomaskioko.tvmaniac.compose.extensions

import android.annotation.SuppressLint
import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import kotlin.math.pow

/**
 * Draws a vertical gradient scrim in the foreground.
 *
 * @param color The color of the gradient scrim.
 * @param startYPercentage The start y value, in percentage of the layout's height (0f to 1f)
 * @param endYPercentage The end y value, in percentage of the layout's height (0f to 1f)
 * @param decay The exponential decay to apply to the gradient. Defaults to `1.0f` which is a linear
 *   gradient.
 * @param numStops The number of color stops to draw in the gradient. Higher numbers result in the
 *   higher visual quality at the cost of draw performance. Defaults to `16`.
 */
@SuppressLint("ComposeModifierComposed")
public fun Modifier.verticalGradientScrim(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) startYPercentage: Float = 0f,
    @FloatRange(from = 0.0, to = 1.0) endYPercentage: Float = 1f,
    decay: Float = 1.0f,
    numStops: Int = 16,
): Modifier = composed {
    val colors = remember(color, numStops) {
        if (decay != 1f) {
            // If we have a non-linear decay, we need to create the color gradient steps
            // manually
            val baseAlpha = color.alpha
            List(numStops) { i ->
                val x = i * 1f / (numStops - 1)
                val opacity = x.pow(decay)
                color.copy(alpha = baseAlpha * opacity)
            }
        } else {
            // If we have a linear decay, we just create a simple list of start + end colors
            listOf(color.copy(alpha = 0f), color)
        }
    }

    var height by remember { mutableFloatStateOf(0f) }
    val brush = remember(color, numStops, startYPercentage, endYPercentage, height) {
        Brush.verticalGradient(
            colors = colors,
            startY = height * startYPercentage,
            endY = height * endYPercentage,
        )
    }

    drawBehind {
        height = size.height
        drawRect(brush = brush)
    }
}

@SuppressLint("ComposeModifierComposed")
internal fun Modifier.iconButtonBackgroundScrim(
    enabled: Boolean,
    shape: Shape = CircleShape,
    @FloatRange(from = 0.0, to = 1.0) alpha: Float,
): Modifier = composed {
    if (enabled) {
        Modifier
            .padding(horizontal = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.background.copy(alpha = alpha),
                shape = shape,
            )
    } else {
        this.padding(horizontal = 4.dp)
    }
}
