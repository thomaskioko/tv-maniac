package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme

@Composable
public fun ShowLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    LinearProgressIndicator(
        progress = { progress },
        color = MaterialTheme.colorScheme.secondary,
        trackColor = if (progress == 1f) {
            TvManiacTheme.colorScheme.success.copy(alpha = 0.5F)
        } else {
            MaterialTheme.colorScheme.secondary.copy(
                alpha = 0.5F,
            )
        },
        strokeCap = StrokeCap.Butt,
        drawStopIndicator = {},
        gapSize = TvManiacSpacing.none,
        modifier = modifier,
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ShowLinearProgressIndicatorPreview() {
    ShowLinearProgressIndicator(progress = 0.6f)
}
