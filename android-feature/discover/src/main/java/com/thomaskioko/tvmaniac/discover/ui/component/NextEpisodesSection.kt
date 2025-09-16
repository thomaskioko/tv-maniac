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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.BoxTextItems
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import com.thomaskioko.tvmaniac.i18n.MR.strings.str_more
import com.thomaskioko.tvmaniac.i18n.resolve
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun NextEpisodesSection(
    title: String,
    modifier: Modifier = Modifier,
    nextEpisodes: ImmutableList<NextEpisodeUiModel>,
    onEpisodeClick: (Long, Long) -> Unit,
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
                    key = { _, episode -> "next_episode_${episode.showId}_${episode.episodeId}" },
                ) { _, episode ->
                    NextEpisodeCard(
                        episode = episode,
                        onEpisodeClick = onEpisodeClick,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun NextEpisodesSectionPreview() {
    TvManiacTheme {
        NextEpisodesSection(
            title = "Up Next",
            nextEpisodes = persistentListOf(
                NextEpisodeUiModel(
                    showId = 1L,
                    showName = "The Walking Dead: Daryl Dixon",
                    showPoster = "/poster1.jpg",
                    episodeId = 123L,
                    episodeTitle = "L'âme Perdue",
                    episodeNumber = "S02E01",
                    runtime = "45 min",
                    stillImage = "/still1.jpg",
                    overview = "Daryl washes ashore in France and struggles to piece together how he got there and why.",
                    isNew = true,
                ),
                NextEpisodeUiModel(
                    showId = 2L,
                    showName = "Wednesday",
                    showPoster = "/poster2.jpg",
                    episodeId = 124L,
                    episodeTitle = "Wednesday's Child Is Full of Woe",
                    episodeNumber = "S02E02",
                    runtime = "50 min",
                    stillImage = "/still2.jpg",
                    overview = "Wednesday arrives at Nevermore Academy and immediately gets off on the wrong foot.",
                    isNew = false,
                ),
                NextEpisodeUiModel(
                    showId = 3L,
                    showName = "House of the Dragon",
                    showPoster = "/poster3.jpg",
                    episodeId = 125L,
                    episodeTitle = "The Heirs of the Dragon",
                    episodeNumber = "S03E01",
                    runtime = "66 min",
                    stillImage = "/still3.jpg",
                    overview = "King Viserys hosts a tournament to celebrate the birth of his second child.",
                    isNew = true,
                ),
            ),
            onEpisodeClick = { _, _ -> },
        )
    }
}
