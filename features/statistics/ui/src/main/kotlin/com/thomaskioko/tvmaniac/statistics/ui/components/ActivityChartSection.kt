package com.thomaskioko.tvmaniac.statistics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.statistics.presenter.model.ActivityBar
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val ChartHeight = 96.dp
private val BarWidth = 8.dp
private const val TRACK_ALPHA = 0.24f

@Composable
internal fun ActivityChartSection(
    bars: ImmutableList<ActivityBar>,
    title: String,
    sectionTestTag: String,
    sectionName: String,
    modifier: Modifier = Modifier,
) {
    CollapsibleSection(
        title = title,
        modifier = modifier.testTag(sectionTestTag),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
            verticalAlignment = Alignment.Bottom,
        ) {
            bars.forEach { bar ->
                ActivityColumn(
                    bar = bar,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(StatisticsTestTags.activityBar(sectionName, bar.label)),
                )
            }
        }
    }
}

@Composable
private fun ActivityColumn(
    bar: ActivityBar,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxSmall),
    ) {
        Box(
            modifier = Modifier
                .height(ChartHeight)
                .width(BarWidth)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = TRACK_ALPHA)),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(bar.fraction.coerceIn(0f, 1f))
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .background(TvManiacTheme.colorScheme.accent),
            )
        }

        Text(
            text = bar.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ActivityChartSectionPreview() {
    ActivityChartSection(
        bars = persistentListOf(
            ActivityBar(label = "Mon", episodeCount = 12, fraction = 1f),
            ActivityBar(label = "Tue", episodeCount = 6, fraction = 0.5f),
            ActivityBar(label = "Wed", episodeCount = 9, fraction = 0.75f),
            ActivityBar(label = "Thu", episodeCount = 3, fraction = 0.25f),
            ActivityBar(label = "Fri", episodeCount = 8, fraction = 0.66f),
            ActivityBar(label = "Sat", episodeCount = 0, fraction = 0f),
            ActivityBar(label = "Sun", episodeCount = 11, fraction = 0.91f),
        ),
        title = "Episodes by day of the week",
        sectionTestTag = StatisticsTestTags.WEEKDAY_ACTIVITY_TEST_TAG,
        sectionName = "weekday",
    )
}
