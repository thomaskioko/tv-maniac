package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.compose.theme.green

@Composable
public fun ShowLinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    LinearProgressIndicator(
        progress = { progress },
        color = MaterialTheme.colorScheme.secondary,
        trackColor = if (progress == 1f) {
            green.copy(alpha = 0.5F)
        } else {
            MaterialTheme.colorScheme.secondary.copy(
                alpha = 0.5F,
            )
        },
        strokeCap = StrokeCap.Butt,
        drawStopIndicator = {},
        gapSize = 0.dp,
        modifier = modifier,
    )
}

@ThemePreviews
@Composable
private fun ShowLinearProgressIndicatorPreview() {
    TvManiacTheme {
        Surface {
            ShowLinearProgressIndicator(progress = 0.6f)
        }
    }
}
