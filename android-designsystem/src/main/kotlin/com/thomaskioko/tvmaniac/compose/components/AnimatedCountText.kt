package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewWrapper

private const val COUNT_UP_DURATION_MILLIS = 280

/**
 * [Text] that counts up from `0` to [count] once when [count] first arrives, then animates
 * between values on subsequent changes. Animation is skipped (final value shown immediately)
 * when motion is reduced or in inspection mode, keeping previews and screenshot tests deterministic.
 *
 * @param count target value to display.
 * @param format renders the displayed integer; defaults to a grouped decimal (e.g. `1,250`).
 */
@Composable
public fun AnimatedCountText(
    count: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    color: Color = Color.Unspecified,
    fontWeight: FontWeight? = null,
    format: (Int) -> String = { "%,d".format(it) },
) {
    val animationsDisabled = LocalInspectionMode.current || rememberReduceMotionEnabled()

    var target by remember { mutableIntStateOf(if (animationsDisabled) count else 0) }
    val animatedValue by animateIntAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = COUNT_UP_DURATION_MILLIS, easing = FastOutSlowInEasing),
        label = "AnimatedCount",
    )

    LaunchedEffect(count, animationsDisabled) {
        target = count
    }

    Text(
        text = format(if (animationsDisabled) count else animatedValue),
        modifier = modifier,
        style = style,
        color = color,
        fontWeight = fontWeight,
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun AnimatedCountTextPreview() {
    AnimatedCountText(count = 1250)
}
