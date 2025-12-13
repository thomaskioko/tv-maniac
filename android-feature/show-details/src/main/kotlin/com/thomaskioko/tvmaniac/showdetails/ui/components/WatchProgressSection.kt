package com.thomaskioko.tvmaniac.showdetails.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.SegmentedProgressBar
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR
import com.thomaskioko.tvmaniac.i18n.MR.strings.title_season_details
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.showdetails.model.SeasonModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun WatchProgressSection(
    status: String?,
    watchedEpisodesCount: Int,
    totalEpisodesCount: Int,
    seasonsList: ImmutableList<SeasonModel>,
    selectedSeasonIndex: Int,
    showHeader: Boolean,
    onSeasonClicked: (Int, SeasonModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (seasonsList.isEmpty()) return

    val context = LocalContext.current
    val remainingEpisodes = totalEpisodesCount - watchedEpisodesCount
    val isUpToDate = remainingEpisodes <= 0 && totalEpisodesCount > 0
    val seasonCount = seasonsList.size

    Column(modifier = modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(24.dp))

        if (showHeader) {
            Text(
                text = title_season_details.resolve(context),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                val headerText = buildAnnotatedString {
                    val tagStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                    val dividerStyle = SpanStyle(
                        color = MaterialTheme.colorScheme.secondary,
                    )

                    status?.let {
                        withStyle(tagStyle) { append(it) }
                        withStyle(dividerStyle) { append(" · ") }
                    }

                    withStyle(tagStyle) {
                        append(
                            context.resources.getQuantityString(
                                MR.plurals.season_count.resourceId,
                                seasonCount,
                                seasonCount,
                            ),
                        )
                    }
                }

                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleMedium,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = pluralStringResource(
                        MR.plurals.episodes_watched.resourceId,
                        totalEpisodesCount,
                        watchedEpisodesCount,
                        totalEpisodesCount,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = if (isUpToDate) {
                        stringResource(MR.strings.label_up_to_date.resourceId)
                    } else {
                        pluralStringResource(
                            MR.plurals.episodes_left.resourceId,
                            remainingEpisodes,
                            remainingEpisodes,
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(12.dp))

                SegmentedProgressBar(
                    segmentProgress = seasonsList.map { it.progressPercentage }.toImmutableList(),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                val lazyListState = rememberLazyListState()

                LaunchedEffect(selectedSeasonIndex) {
                    if (selectedSeasonIndex > 0 && selectedSeasonIndex < seasonsList.size) {
                        lazyListState.animateScrollToItem(
                            index = selectedSeasonIndex,
                            scrollOffset = -50,
                        )
                    }
                }

                LazyRow(
                    state = lazyListState,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                ) {
                    itemsIndexed(
                        items = seasonsList,
                        key = { index, item -> "${item.tvShowId}_${item.seasonId}_$index" },
                    ) { index, season ->
                        SeasonProgressCard(
                            season = season,
                            isSelected = index == selectedSeasonIndex,
                            onClick = { onSeasonClicked(index, season) },
                        )
                    }
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun WatchProgressSectionPreview() {
    TvManiacTheme {
        Surface {
            WatchProgressSection(
                status = "Ended",
                watchedEpisodesCount = 7,
                totalEpisodesCount = 12,
                seasonsList = persistentListOf(
                    SeasonModel(
                        seasonId = 1L,
                        tvShowId = 1L,
                        name = "Season 1",
                        seasonNumber = 1L,
                        watchedCount = 6,
                        totalCount = 6,
                    ),
                    SeasonModel(
                        seasonId = 2L,
                        tvShowId = 1L,
                        name = "Season 2",
                        seasonNumber = 2L,
                        watchedCount = 1,
                        totalCount = 6,
                    ),
                ),
                selectedSeasonIndex = 0,
                showHeader = true,
                onSeasonClicked = { _, _ -> },
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun WatchProgressSectionUpToDatePreview() {
    TvManiacTheme {
        Surface {
            WatchProgressSection(
                status = "Returning Series",
                watchedEpisodesCount = 30,
                totalEpisodesCount = 30,
                seasonsList = persistentListOf(
                    SeasonModel(
                        seasonId = 1L,
                        tvShowId = 1L,
                        name = "Season 1",
                        seasonNumber = 1L,
                        watchedCount = 6,
                        totalCount = 6,
                    ),
                    SeasonModel(
                        seasonId = 2L,
                        tvShowId = 1L,
                        name = "Season 2",
                        seasonNumber = 2L,
                        watchedCount = 6,
                        totalCount = 6,
                    ),
                    SeasonModel(
                        seasonId = 3L,
                        tvShowId = 1L,
                        name = "Season 3",
                        seasonNumber = 3L,
                        watchedCount = 6,
                        totalCount = 6,
                    ),
                    SeasonModel(
                        seasonId = 4L,
                        tvShowId = 1L,
                        name = "Season 4",
                        seasonNumber = 4L,
                        watchedCount = 6,
                        totalCount = 6,
                    ),
                    SeasonModel(
                        seasonId = 5L,
                        tvShowId = 1L,
                        name = "Season 5",
                        seasonNumber = 5L,
                        watchedCount = 6,
                        totalCount = 6,
                    ),
                ),
                selectedSeasonIndex = 0,
                showHeader = true,
                onSeasonClicked = { _, _ -> },
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@ThemePreviews
@Composable
private fun WatchProgressSectionUntrackedPreview() {
    TvManiacTheme {
        Surface {
            WatchProgressSection(
                status = "Ended",
                watchedEpisodesCount = 0,
                totalEpisodesCount = 12,
                seasonsList = persistentListOf(
                    SeasonModel(
                        seasonId = 1L,
                        tvShowId = 1L,
                        name = "Season 1",
                        seasonNumber = 1L,
                        watchedCount = 0,
                        totalCount = 6,
                    ),
                    SeasonModel(
                        seasonId = 2L,
                        tvShowId = 1L,
                        name = "Season 2",
                        seasonNumber = 2L,
                        watchedCount = 0,
                        totalCount = 6,
                    ),
                ),
                selectedSeasonIndex = 0,
                showHeader = true,
                onSeasonClicked = { _, _ -> },
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
