package com.thomaskioko.tvmaniac.discover.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import com.thomaskioko.tvmaniac.i18n.MR.strings.str_more
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun NextEpisodesSection(
    title: String,
    modifier: Modifier = Modifier,
    nextEpisodes: ImmutableList<NextEpisodeUiModel>,
    onEpisodeClick: (NextEpisodeUiModel) -> Unit,
    onSeeAllClick: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = nextEpisodes.isNotEmpty(),
        modifier = modifier,
    ) {
        Column {
            BoxTextItems(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                title = title,
                label = str_more.resolve(LocalContext.current),
                onMoreClicked = onSeeAllClick,
            )

            val lazyListState = rememberLazyListState()

            LazyRow(
                state = lazyListState,
                flingBehavior = rememberSnapperFlingBehavior(lazyListState),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(
                    items = nextEpisodes,
                    key = { _, episode -> "next_episode_${episode.showTraktId}_${episode.episodeId}" },
                ) { _, episode ->
                    NextEpisodeCard(
                        modifier = Modifier.testTag(DiscoverTestTags.upNextCard(episode.showTraktId)),
                        episode = episode,
                        onClick = { onEpisodeClick(episode) },
                    )
                }
            }
        }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun NextEpisodesSectionPreview() {
    NextEpisodesSection(
        title = "Up Next",
        nextEpisodes = persistentListOf(
            NextEpisodeUiModel(
                showTraktId = 1L,
                showName = "The Walking Dead: Daryl Dixon",
                imageUrl = "/still1.jpg",
                episodeId = 123L,
                episodeTitle = "L'âme Perdue",
                episodeNumberFormatted = "S02E01",
                seasonId = 1L,
                seasonNumber = 2,
                episodeNumber = 1,
                runtime = "45 min",
                overview = "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                isNew = true,
            ),
            NextEpisodeUiModel(
                showTraktId = 2L,
                showName = "Wednesday",
                imageUrl = "/still1.jpg",
                episodeId = 124L,
                episodeTitle = "Wednesday's Child Is Full of Woe",
                episodeNumberFormatted = "S02E02",
                seasonId = 2L,
                seasonNumber = 2,
                episodeNumber = 2,
                runtime = "50 min",
                overview = "Wednesday arrives at Nevermore Academy and immediately gets off on the wrong foot.",
                isNew = false,
            ),
            NextEpisodeUiModel(
                showTraktId = 3L,
                showName = "House of the Dragon",
                imageUrl = "/still1.jpg",
                episodeId = 125L,
                episodeTitle = "The Heirs of the Dragon",
                episodeNumberFormatted = "S03E01",
                seasonId = 3L,
                seasonNumber = 3,
                episodeNumber = 1,
                runtime = "66 min",
                overview = "King Viserys hosts a tournament to celebrate the birth of his second child.",
                isNew = true,
            ),
        ),
        onEpisodeClick = {},
    )
}
