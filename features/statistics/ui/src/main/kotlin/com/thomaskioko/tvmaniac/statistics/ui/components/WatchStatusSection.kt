package com.thomaskioko.tvmaniac.statistics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.CollapsibleSection
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchStatusItem
import com.thomaskioko.tvmaniac.statistics.presenter.model.WatchStatusItemId
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun WatchStatusSection(
    items: ImmutableList<WatchStatusItem>,
    title: String,
    modifier: Modifier = Modifier,
) {
    CollapsibleSection(
        title = title,
        modifier = modifier.testTag(StatisticsTestTags.WATCH_STATUS_SECTION_TEST_TAG),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TvManiacSpacing.medium),
            verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
        ) {
            items.forEach { item ->
                WatchStatusRow(
                    item = item,
                    modifier = Modifier.testTag(StatisticsTestTags.watchStatusRow(item.id.name)),
                )
            }
        }
    }
}

@Composable
private fun WatchStatusRow(
    item: WatchStatusItem,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = item.showCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.height(TvManiacSpacing.xxxSmall))

        StatisticBar(
            fraction = item.fraction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun WatchStatusSectionPreview() {
    WatchStatusSection(
        items = persistentListOf(
            WatchStatusItem(id = WatchStatusItemId.Watchlist, label = "Watchlist", showCount = 24, fraction = 0.4f),
            WatchStatusItem(id = WatchStatusItemId.Watching, label = "Watching", showCount = 6, fraction = 0.1f),
            WatchStatusItem(id = WatchStatusItemId.Completed, label = "Completed", showCount = 28, fraction = 0.46f),
            WatchStatusItem(id = WatchStatusItemId.OnHold, label = "On hold", showCount = 2, fraction = 0.03f),
            WatchStatusItem(id = WatchStatusItemId.Dropped, label = "Dropped", showCount = 1, fraction = 0.01f),
        ),
        title = "Shows by status",
    )
}
