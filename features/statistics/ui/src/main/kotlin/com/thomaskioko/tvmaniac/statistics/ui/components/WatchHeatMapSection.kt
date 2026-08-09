package com.thomaskioko.tvmaniac.statistics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.statistics.presenter.model.HeatMap
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchHeatMap
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags

private val CellSize = 10.dp
private val TodayBorderWidth = 1.dp
private const val DAYS_IN_WEEK = 7
private const val TRACK_ALPHA = 0.16f

@Composable
internal fun WatchHeatMapSection(
    heatMap: WatchHeatMap,
    title: String,
    modifier: Modifier = Modifier,
) {
    CollapsibleSection(
        title = title,
        modifier = modifier.testTag(StatisticsTestTags.HEAT_MAP_TEST_TAG),
    ) {
        val today = heatMap.cells.lastOrNull()?.date

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = TvManiacSpacing.medium)
                    .clearAndSetSemantics {},
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxxSmall),
            ) {
                heatMap.toColumns().forEach { column ->
                    Column(verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxxSmall)) {
                        column.forEach { cell ->
                            HeatMapCell(cell = cell, isToday = cell != null && cell.date == today)
                        }
                    }
                }
            }

            HeatMapLegend(
                busiestDayCount = heatMap.busiestDayCount,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = TvManiacSpacing.medium, vertical = TvManiacSpacing.small),
            )
        }
    }
}

@Composable
private fun HeatMapLegend(
    busiestDayCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.testTag(StatisticsTestTags.HEAT_MAP_LEGEND_TEST_TAG),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xxxSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "0",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        (0..MAX_LEVEL.toInt()).forEach { level ->
            Box(
                modifier = Modifier
                    .size(CellSize)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(levelColor(level)),
            )
        }
        Text(
            text = "$busiestDayCount",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun HeatMapCell(
    cell: HeatMap?,
    isToday: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(CellSize)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(cell.color())
            .then(
                if (isToday) {
                    Modifier.border(
                        width = TodayBorderWidth,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                } else {
                    Modifier
                },
            ),
    )
}

@Composable
private fun HeatMap?.color(): Color = when (this?.level) {
    null -> Color.Transparent
    else -> levelColor(level)
}

@Composable
private fun levelColor(level: Int): Color = when (level) {
    0 -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = TRACK_ALPHA)
    else -> TvManiacTheme.colorScheme.accent.copy(alpha = level / MAX_LEVEL)
}

/**
 * Lays the dense day series out column by column so each row is the same weekday, padding the
 * first column with the blanks the mapper counted.
 */
private fun WatchHeatMap.toColumns(): List<List<HeatMap?>> =
    (List<HeatMap?>(leadingBlankCells) { null } + cells).chunked(DAYS_IN_WEEK)

private const val MAX_LEVEL = 4f

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WatchHeatMapSectionPreview() {
    WatchHeatMapSection(
        heatMap = WatchHeatMap(
            cells = List(70) { index ->
                HeatMap(
                    date = "2026-06-${(index % 28) + 1}",
                    level = index % 5,
                    episodeCount = index % 5,
                )
            },
            leadingBlankCells = 3,
            activeDays = 56,
            quietDays = 14,
            busiestDayCount = 4,
        ),
        title = "Your last year of watching",
    )
}
