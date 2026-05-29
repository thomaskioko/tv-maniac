package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalInspectionMode

private const val SHIMMER_DURATION_MILLIS = 1200

/**
 * Draws a sweeping highlight over a translucent base, signalling content is loading. Falls back to
 * a static base when motion is reduced or in inspection mode, so previews and screenshot tests stay
 * deterministic. Apply [clip] before this to confine the sweep to a shape.
 */
@Composable
public fun Modifier.shimmer(): Modifier {
    val animationsDisabled = LocalInspectionMode.current || rememberReduceMotionEnabled()
    val baseColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    val highlightColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)

    if (animationsDisabled) {
        return this.background(baseColor)
    }

    val transition = rememberInfiniteTransition(label = "Shimmer")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_DURATION_MILLIS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ShimmerProgress",
    )

    return this
        .background(baseColor)
        .drawBehind {
            val width = size.width
            val start = progress.value * 2f * width - width
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, highlightColor, Color.Transparent),
                    start = Offset(start, 0f),
                    end = Offset(start + width, size.height),
                ),
            )
        }
}

/** Box pre-clipped to [shape] that renders a [shimmer]. Sized by the caller via [modifier]. */
@Composable
public fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small,
) {
    Box(modifier = modifier.clip(shape).shimmer())
}
