package com.thomaskioko.tvmaniac.myshows.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.myshows.presenter.MyShowsState
import com.thomaskioko.tvmaniac.myshows.presenter.model.EpisodeBadge
import com.thomaskioko.tvmaniac.myshows.presenter.model.MyShowsItem
import com.thomaskioko.tvmaniac.myshows.presenter.model.UpNextEpisodeItem
import kotlinx.collections.immutable.toPersistentList

internal val myShowsItems = List(3) { index ->
    MyShowsItem(
        traktId = 84958L + index,
        title = "Loki",
        posterImageUrl = null,
        year = "2021",
        status = "Canceled",
        seasonCount = 6,
        episodeCount = 12,
        watchProgress = 0.3f + (index * 0.2f),
    )
}.toPersistentList()

internal val staleMyShowsItems = List(2) { index ->
    MyShowsItem(
        traktId = 94958L + index,
        title = "The Mandalorian",
        posterImageUrl = null,
        year = "2019",
        status = "Returning Series",
        seasonCount = 3,
        episodeCount = 24,
        watchProgress = 0.5f,
        lastWatchedAt = System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000L),
    )
}.toPersistentList()

internal val watchNextEpisodes = listOf(
    UpNextEpisodeItem(
        showTraktId = 84958L,
        showName = "Loki",
        showPoster = null,
        episodeId = 1L,
        episodeTitle = "Glorious Purpose",
        episodeNumberFormatted = "S01 | E01",
        seasonId = 1L,
        seasonNumber = 1,
        episodeNumber = 1,
        runtime = "52 min",
        stillImage = null,
        overview = "After stealing the Tesseract in Avengers: Endgame, Loki lands before the Time Variance Authority.",
        badge = EpisodeBadge.PREMIERE,
        remainingEpisodes = 5,
    ),
    UpNextEpisodeItem(
        showTraktId = 95557L,
        showName = "The Walking Dead",
        showPoster = null,
        episodeId = 12L,
        episodeTitle = "What We Become",
        episodeNumberFormatted = "S10 | E13",
        seasonId = 10L,
        seasonNumber = 10,
        episodeNumber = 13,
        runtime = "45 min",
        stillImage = null,
        overview = "Michonne takes Virgil back to his island to get weapons.",
        badge = EpisodeBadge.NEW,
        remainingEpisodes = 8,
    ),
).toPersistentList()

internal val staleEpisodes = listOf(
    UpNextEpisodeItem(
        showTraktId = 94958L,
        showName = "The Mandalorian",
        showPoster = null,
        episodeId = 5L,
        episodeTitle = "The Gunslinger",
        episodeNumberFormatted = "S01 | E05",
        seasonId = 1L,
        seasonNumber = 1,
        episodeNumber = 5,
        runtime = "35 min",
        stillImage = null,
        overview = "The Mandalorian helps a rookie bounty hunter on Tatooine.",
        remainingEpisodes = 3,
        lastWatchedAt = System.currentTimeMillis() - (14 * 24 * 60 * 60 * 1000L),
    ),
).toPersistentList()

internal class MyShowsPreviewParameterProvider : PreviewParameterProvider<MyShowsState> {
    override val values: Sequence<MyShowsState>
        get() {
            return sequenceOf(
                MyShowsState(
                    isRefreshing = false,
                    isGridMode = false,
                    watchNextItems = myShowsItems,
                    staleItems = staleMyShowsItems,
                    watchNextEpisodes = watchNextEpisodes,
                    staleEpisodes = staleEpisodes,
                ),
                MyShowsState(
                    isRefreshing = false,
                    isGridMode = true,
                    watchNextItems = myShowsItems,
                    staleItems = staleMyShowsItems,
                    watchNextEpisodes = watchNextEpisodes,
                    staleEpisodes = staleEpisodes,
                ),
                MyShowsState(
                    isGridMode = false,
                    isRefreshing = false,
                    isSearchActive = true,
                    watchNextItems = myShowsItems,
                    staleItems = staleMyShowsItems,
                    watchNextEpisodes = watchNextEpisodes,
                    staleEpisodes = staleEpisodes,
                ),
                MyShowsState(
                    isRefreshing = false,
                    watchNextItems = myShowsItems,
                    staleItems = staleMyShowsItems,
                    watchNextEpisodes = watchNextEpisodes,
                    staleEpisodes = staleEpisodes,
                    message = UiMessage(message = "Something went Wrong"),
                ),
                MyShowsState(),
                MyShowsState(
                    message = UiMessage(message = "Something went Wrong"),
                ),
            )
        }
}
