package com.thomaskioko.tvmaniac.discover.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverViewState
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import kotlinx.collections.immutable.toImmutableList

internal val discoverShow = DiscoverShow(
    tmdbId = 84958,
    traktId = 84958,
    title = "Loki",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    overView = "After stealing the Tesseract during the events of Avengers: Endgame, an ",
)

internal val nextEpisodeUiModel = NextEpisodeUiModel(
    showTraktId = 1L,
    showName = "The Walking Dead: Daryl Dixon",
    showPoster = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    episodeId = 123L,
    episodeTitle = "L'âme Perdue",
    episodeNumberFormatted = "S02E01",
    seasonId = 1L,
    seasonNumber = 2,
    episodeNumber = 1,
    runtime = "45 min",
    stillImage = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    overview = "Daryl washes ashore in France and struggles to piece together how he got there and why.",
    isNew = true,
)

internal val discoverContentSuccess = DiscoverViewState(
    featuredShows = createDiscoverShowList(5),
    topRatedShows = createDiscoverShowList(),
    popularShows = createDiscoverShowList(),
    upcomingShows = createDiscoverShowList(),
    trendingToday = createDiscoverShowList(),
    nextEpisodes = createNextEpisodesList(3),
)

private fun createDiscoverShowList(size: Int = 20) = List(size) { discoverShow }.toImmutableList()

private fun createNextEpisodesList(size: Int = 3) = List(size) { index ->
    nextEpisodeUiModel.copy(
        showTraktId = (index + 1).toLong(),
        showName = when (index) {
            0 -> "The Walking Dead: Daryl Dixon"
            1 -> "Wednesday"
            else -> "House of the Dragon"
        },
        episodeNumberFormatted = "S0${index + 2}E0${index + 1}",
        seasonId = (index + 1).toLong(),
        seasonNumber = (index + 2).toLong(),
        episodeNumber = (index + 1).toLong(),
        episodeTitle = when (index) {
            0 -> "L'âme Perdue"
            1 -> "Wednesday's Child Is Full of Woe"
            else -> "The Heirs of the Dragon"
        },
    )
}.toImmutableList()

internal class DiscoverPreviewParameterProvider : PreviewParameterProvider<DiscoverViewState> {
    override val values: Sequence<DiscoverViewState>
        get() {
            return sequenceOf(
                DiscoverViewState.Empty,
                discoverContentSuccess,
                DiscoverViewState(
                    message = UiMessage(
                        "Opps! Something went wrong",
                    ),
                ),
            )
        }
}
