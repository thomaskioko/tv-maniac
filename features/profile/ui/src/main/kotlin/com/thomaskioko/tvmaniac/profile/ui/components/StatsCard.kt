package com.thomaskioko.tvmaniac.profile.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileLabels
import com.thomaskioko.tvmaniac.profile.presenter.model.ProfileStats
import com.thomaskioko.tvmaniac.profile.ui.StatTile
import com.thomaskioko.tvmaniac.profile.ui.StatValueLabel
import com.thomaskioko.tvmaniac.profile.ui.StatValueText

@Composable
internal fun StatsCard(
    stats: ProfileStats,
    labels: ProfileLabels,
    listCount: Int,
    onViewLists: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollapsibleSection(
        title = labels.statsTitle,
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = TvManiacSpacing.medium)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
            ) {
                EpisodesWatchedCard(
                    title = labels.episodesWatched,
                    value = stats.episodesWatched,
                    modifier = Modifier.weight(1f),
                )

                ShowsWatchedCard(
                    title = labels.showsWatched,
                    value = stats.showsWatched,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(TvManiacSpacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
            ) {
                WatchTimeCard(
                    title = labels.watchTime,
                    months = stats.months,
                    days = stats.days,
                    hours = stats.hours,
                    monthsLabel = labels.monthsShort,
                    daysLabel = labels.daysShort,
                    hoursLabel = labels.hoursShort,
                    modifier = Modifier.weight(1f),
                )

                ListsCard(
                    title = labels.lists,
                    count = listCount,
                    viewButtonLabel = labels.viewButton,
                    onViewLists = onViewLists,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
internal fun EpisodesWatchedCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    StatTile(
        imageVector = Icons.Filled.PlayCircle,
        title = title,
        modifier = modifier,
    ) {
        StatValueLabel(text = value)
    }
}

@Composable
internal fun ShowsWatchedCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    StatTile(
        imageVector = Icons.Filled.Tv,
        title = title,
        modifier = modifier,
    ) {
        StatValueLabel(text = value)
    }
}

@Composable
internal fun WatchTimeCard(
    title: String,
    months: Int,
    days: Int,
    hours: Int,
    monthsLabel: String,
    daysLabel: String,
    hoursLabel: String,
    modifier: Modifier = Modifier,
) {
    StatTile(
        imageVector = Icons.Filled.Schedule,
        title = title,
        modifier = modifier,
    ) {
        WatchTimeValue(
            months = months,
            days = days,
            hours = hours,
            monthsLabel = monthsLabel,
            daysLabel = daysLabel,
            hoursLabel = hoursLabel,
        )
    }
}

@Composable
internal fun ListsCard(
    title: String,
    count: Int,
    viewButtonLabel: String,
    onViewLists: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatTile(
        imageVector = Icons.AutoMirrored.Filled.List,
        title = title,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatValueText(count = count)
            Text(
                text = viewButtonLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = onViewLists)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(horizontal = TvManiacSpacing.small, vertical = TvManiacSpacing.xxSmall),
            )
        }
    }
}

@Composable
private fun WatchTimeValue(
    months: Int,
    days: Int,
    hours: Int,
    monthsLabel: String,
    daysLabel: String,
    hoursLabel: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.xSmall),
        verticalAlignment = Alignment.Bottom,
    ) {
        WatchTimeSegment(value = months, unit = monthsLabel)
        WatchTimeSegment(value = days, unit = daysLabel)
        WatchTimeSegment(value = hours, unit = hoursLabel)
    }
}

@Composable
private fun WatchTimeSegment(
    value: Int,
    unit: String,
) {
    Row(verticalAlignment = Alignment.Bottom) {
        StatValueText(
            count = value,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.alignByBaseline(),
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false,
            modifier = Modifier
                .alignByBaseline()
                .padding(start = TvManiacSpacing.xxxSmall),
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun StatsCardPreview() {
    StatsCard(
        stats = ProfileStats(
            showsWatched = "42",
            episodesWatched = "256",
            months = 2,
            days = 5,
            hours = 12,
        ),
        labels = ProfileLabels(
            statsTitle = "Stats",
            episodesWatched = "Episodes Watched",
            showsWatched = "Shows Watched",
            watchTime = "Watch Time",
            monthsShort = "M",
            daysShort = "D",
            hoursShort = "H",
            lists = "Lists",
            viewButton = "View",
        ),
        listCount = 8,
        onViewLists = {},
        modifier = Modifier.padding(TvManiacSpacing.medium),
    )
}
