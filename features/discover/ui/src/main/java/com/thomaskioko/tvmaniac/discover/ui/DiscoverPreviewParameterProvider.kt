package com.thomaskioko.tvmaniac.discover.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverViewState
import com.thomaskioko.tvmaniac.discover.presenter.catalog.DiscoverCatalogState
import com.thomaskioko.tvmaniac.discover.presenter.featured.DiscoverFeaturedState
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import com.thomaskioko.tvmaniac.discover.presenter.startwatching.DiscoverStartWatchingState
import com.thomaskioko.tvmaniac.discover.presenter.upnext.DiscoverUpNextState
import kotlinx.collections.immutable.toImmutableList

internal val discoverShow = DiscoverShow(
    tmdbId = 84958,
    showId = 84958,
    title = "Loki",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    overView = "After stealing the Tesseract during the events of Avengers: Endgame, an ",
)

internal val nextEpisodeUiModel = NextEpisodeUiModel(
    showId = 1L,
    showName = "The Walking Dead: Daryl Dixon",
    imageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    episodeId = 123L,
    episodeTitle = "L'âme Perdue",
    episodeNumberFormatted = "S02E01",
    seasonId = 1L,
    seasonNumber = 2,
    episodeNumber = 1,
    runtime = "45 min",
    overview = "Daryl washes ashore in France and struggles to piece together how he got there and why.",
    isNew = true,
)

internal val discoverFeaturedContentSuccess = DiscoverFeaturedState(
    isInitial = false,
    loading = false,
    featuredShows = createDiscoverShowList(5),
)

internal val discoverCatalogContentSuccess = DiscoverCatalogState(
    isInitial = false,
    loading = false,
    trendingShows = createDiscoverShowList(),
    upcomingShows = createDiscoverShowList(),
    popularShows = createDiscoverShowList(),
    topRatedShows = createDiscoverShowList(),
    trendingTitle = "Trending Today",
    upcomingTitle = "Upcoming",
    popularTitle = "Popular",
    topRatedTitle = "Top Rated",
)

internal val discoverUpNextContentSuccess = DiscoverUpNextState(
    nextEpisodes = createNextEpisodesList(3),
)

internal val discoverStartWatchingContentSuccess = DiscoverStartWatchingState(
    startWatchingShows = createDiscoverShowList().map { it.copy(inLibrary = true) }.toImmutableList(),
    startWatchingTitle = "Start Watching",
)

internal fun createDiscoverShowList(size: Int = 20) = List(size) { index ->
    discoverShow.copy(
        tmdbId = discoverShow.tmdbId + index,
        showId = discoverShow.showId + index,
    )
}.toImmutableList()

internal fun createNextEpisodesList(size: Int = 3) = List(size) { index ->
    nextEpisodeUiModel.copy(
        showId = (index + 1).toLong(),
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

internal data class DiscoverPreviewState(
    val hostState: DiscoverViewState,
    val featuredState: DiscoverFeaturedState,
    val catalogState: DiscoverCatalogState,
    val upNextState: DiscoverUpNextState,
    val startWatchingState: DiscoverStartWatchingState,
)

internal class DiscoverPreviewParameterProvider : PreviewParameterProvider<DiscoverPreviewState> {
    override val values: Sequence<DiscoverPreviewState>
        get() = sequenceOf(
            DiscoverPreviewState(
                hostState = DiscoverViewState(isLoading = true),
                featuredState = DiscoverFeaturedState(),
                catalogState = DiscoverCatalogState(),
                upNextState = DiscoverUpNextState(),
                startWatchingState = DiscoverStartWatchingState(),
            ),
            DiscoverPreviewState(
                hostState = DiscoverViewState(isEmpty = true),
                featuredState = DiscoverFeaturedState(isInitial = false),
                catalogState = DiscoverCatalogState(isInitial = false),
                upNextState = DiscoverUpNextState(),
                startWatchingState = DiscoverStartWatchingState(),
            ),
            DiscoverPreviewState(
                hostState = DiscoverViewState(),
                featuredState = discoverFeaturedContentSuccess,
                catalogState = discoverCatalogContentSuccess,
                upNextState = discoverUpNextContentSuccess,
                startWatchingState = discoverStartWatchingContentSuccess,
            ),
            DiscoverPreviewState(
                hostState = DiscoverViewState(
                    showError = true,
                    message = UiMessage("Opps! Something went wrong"),
                ),
                featuredState = DiscoverFeaturedState(
                    isInitial = false,
                    message = UiMessage("Opps! Something went wrong"),
                ),
                catalogState = DiscoverCatalogState(
                    isInitial = false,
                    message = UiMessage("Opps! Something went wrong"),
                ),
                upNextState = DiscoverUpNextState(),
                startWatchingState = DiscoverStartWatchingState(),
            ),
        )
}
