package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
public fun SegmentedProgressBar(
    segmentProgress: ImmutableList<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    segmentGap: Dp = 4.dp,
    trackColor: Color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
) {
    if (segmentProgress.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(segmentGap),
    ) {
        segmentProgress.forEach { progress ->
            ProgressSegment(
                progress = progress,
                modifier = Modifier.weight(1f),
                height = height,
                trackColor = trackColor,
            )
        }
    }
}

@Composable
private fun ProgressSegment(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 6.dp,
    trackColor: Color,
) {
    val shape = RoundedCornerShape(height / 2)
    val progressColor = MaterialTheme.colorScheme.secondary

    Box(
        modifier = modifier
            .height(height)
            .clip(shape)
            .background(trackColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                .height(height)
                .clip(shape)
                .background(progressColor),
        )
    }
}

@ThemePreviews
@Composable
private fun SegmentedProgressBarPreview() {
    TvManiacTheme {
        Surface {
            SegmentedProgressBar(
                segmentProgress = persistentListOf(1f, 0.5f, 0f),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun SegmentedProgressBarSinglePreview() {
    TvManiacTheme {
        Surface {
            SegmentedProgressBar(
                segmentProgress = persistentListOf(0.75f),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
