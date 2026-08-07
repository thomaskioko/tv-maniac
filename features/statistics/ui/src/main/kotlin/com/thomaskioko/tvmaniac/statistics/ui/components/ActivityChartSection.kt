package com.thomaskioko.tvmaniac.statistics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.statistics.presenter.model.ActivityBar
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val ChartHeight = 120.dp
private val BarCorner = 4.dp
private val TooltipCorner = 8.dp
private val SectionTitleTracking = 1.sp

/** A bar with no watches still shows a sliver so the axis reads as continuous. */
private const val MIN_BAR_FRACTION = 0.02f

/** The busiest bar is drawn at full strength and the quietest at this much of it. */
private const val DIMMEST_BAR_ALPHA = 0.45f
private const val AXIS_LABEL_COUNT = 4

@Composable
internal fun ActivityChartSection(
    bars: ImmutableList<ActivityBar>,
    title: String,
    sectionTestTag: String,
    sectionName: String,
    modifier: Modifier = Modifier,
    showAxisLabels: Boolean = false,
) {
    var selectedLabel by remember(bars) { mutableStateOf<String?>(null) }
    val selected = bars.firstOrNull { it.label == selectedLabel }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.medium)
            .testTag(sectionTestTag),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ChartHeight),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxxSmall),
                verticalAlignment = Alignment.Bottom,
            ) {
                bars.forEach { bar ->
                    ActivityBarColumn(
                        bar = bar,
                        isSelected = bar.label == selectedLabel,
                        onClick = { selectedLabel = if (bar.label == selectedLabel) null else bar.label },
                        modifier = Modifier
                            .weight(1f)
                            .testTag(StatisticsTestTags.activityBar(sectionName, bar.label)),
                    )
                }
            }

            selected?.let { bar ->
                ActivityTooltip(
                    bar = bar,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .testTag(StatisticsTestTags.ACTIVITY_TOOLTIP_TEST_TAG),
                )
            }
        }

        if (showAxisLabels) {
            AxisLabels(bars = bars, modifier = Modifier.padding(top = TvManiacSpacing.xxSmall))
        }

        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = SectionTitleTracking,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = TvManiacSpacing.small),
        )
    }
}

@Composable
private fun ActivityBarColumn(
    bar: ActivityBar,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val strength = if (isSelected) 1f else DIMMEST_BAR_ALPHA + (1f - DIMMEST_BAR_ALPHA) * bar.fraction

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(bar.fraction.coerceIn(MIN_BAR_FRACTION, 1f))
                .clip(RoundedCornerShape(BarCorner))
                .background(TvManiacTheme.colorScheme.accent.copy(alpha = strength)),
        )
    }
}

@Composable
private fun ActivityTooltip(
    bar: ActivityBar,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(TooltipCorner))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(TooltipCorner),
            )
            .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = bar.caption,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = bar.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun AxisLabels(
    bars: ImmutableList<ActivityBar>,
    modifier: Modifier = Modifier,
) {
    val step = ((bars.size - 1) / (AXIS_LABEL_COUNT - 1)).coerceAtLeast(1)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = TvManiacSpacing.medium),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxxSmall),
    ) {
        bars.forEachIndexed { index, bar ->
            Text(
                text = if (index % step == 0) bar.label else "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun ActivityChartSectionPreview() {
    ActivityChartSection(
        bars = persistentListOf(
            ActivityBar(label = "Mon", episodeCount = 12, caption = "12 episodes", fraction = 0.6f),
            ActivityBar(label = "Tue", episodeCount = 8, caption = "8 episodes", fraction = 0.4f),
            ActivityBar(label = "Wed", episodeCount = 14, caption = "14 episodes", fraction = 0.7f),
            ActivityBar(label = "Thu", episodeCount = 6, caption = "6 episodes", fraction = 0.3f),
            ActivityBar(label = "Fri", episodeCount = 17, caption = "17 episodes", fraction = 0.85f),
            ActivityBar(label = "Sat", episodeCount = 20, caption = "20 episodes", fraction = 1f),
            ActivityBar(label = "Sun", episodeCount = 0, caption = "0 episodes", fraction = 0f),
        ),
        title = "Episodes by day of the week",
        sectionTestTag = StatisticsTestTags.WEEKDAY_ACTIVITY_TEST_TAG,
        sectionName = "weekday",
    )
}
