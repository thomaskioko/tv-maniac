package com.thomaskioko.tvmaniac.statistics.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacElevation
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTile
import com.thomaskioko.tvmaniac.statistics.presenter.model.StatisticTileId
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private const val TILE_GRID_COLUMNS = 2

@Composable
internal fun StatisticTileGrid(
    tiles: ImmutableList<StatisticTile>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = TvManiacSpacing.medium)
            .testTag(StatisticsTestTags.TILE_GRID_TEST_TAG),
        verticalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
    ) {
        tiles.chunked(TILE_GRID_COLUMNS).forEach { rowTiles ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(TvManiacSpacing.small),
            ) {
                rowTiles.forEach { tile ->
                    StatisticTileCard(
                        tile = tile,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .testTag(StatisticsTestTags.tile(tile.id.name)),
                    )
                }
                if (rowTiles.size < TILE_GRID_COLUMNS) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatisticTileCard(
    tile: StatisticTile,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = TvManiacElevation.small),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(TvManiacSpacing.medium)) {
            Text(
                text = tile.label,
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = SectionLabelLetterSpacing,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(TvManiacSpacing.xxSmall))

            Text(
                text = tile.value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = TvManiacTheme.colorScheme.accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            if (tile.caption.isNotEmpty()) {
                Spacer(modifier = Modifier.height(TvManiacSpacing.xxxSmall))

                Text(
                    text = tile.caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun StatisticTileGridPreview() {
    StatisticTileGrid(
        tiles = persistentListOf(
            StatisticTile(
                id = StatisticTileId.TitlesTracked,
                label = "Titles tracked",
                value = "128",
                caption = "18 completed",
            ),
            StatisticTile(
                id = StatisticTileId.Episodes,
                label = "Episodes",
                value = "1.4K",
                caption = "",
            ),
            StatisticTile(
                id = StatisticTileId.AverageRating,
                label = "Average rating",
                value = "8.4",
                caption = "across 42 rated",
            ),
            StatisticTile(
                id = StatisticTileId.WatchStreak,
                label = "Watch streak",
                value = "9 days",
                caption = "longest run",
            ),
            StatisticTile(
                id = StatisticTileId.WatchDaysThisYear,
                label = "Watch days this year",
                value = "112/214",
                caption = "days you watched something",
            ),
        ),
    )
}
