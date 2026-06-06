package com.thomaskioko.tvmaniac.continuewatching.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.continuewatching.presenter.ContinueWatchingLabels
import com.thomaskioko.tvmaniac.continuewatching.presenter.ContinueWatchingState
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.ContinueWatchingItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.EpisodeBadge
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.core.view.UiMessage
import kotlinx.collections.immutable.toPersistentList

internal val continueWatchingItems = List(3) { index ->
    ContinueWatchingItem(
        showId = 84958L + index,
        title = "Loki",
        posterImageUrl = null,
        year = "2021",
        status = "Canceled",
        seasonCount = 6,
        episodeCount = 12,
        watchProgress = 0.3f + (index * 0.2f),
    )
}.toPersistentList()

internal val staleContinueWatchingItems = List(2) { index ->
    ContinueWatchingItem(
        showId = 94958L + index,
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
        showId = 84958L,
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
        showId = 95557L,
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
        showId = 94958L,
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

internal val previewLabels = ContinueWatchingLabels(
    watchingTitle = "Watching",
    staleTitle = "Haven't Watched For A While",
    upToDate = "All caught up",
    premiereBadge = "PREMIERE",
    newBadge = "NEW",
    emptyTitle = "Nothing in progress yet. Mark an episode as watched to see it here.",
)

internal class ContinueWatchingPreviewParameterProvider : PreviewParameterProvider<ContinueWatchingState> {
    override val values: Sequence<ContinueWatchingState>
        get() {
            return sequenceOf(
                ContinueWatchingState(
                    isRefreshing = false,
                    isGridMode = false,
                    labels = previewLabels,
                    watchNextItems = continueWatchingItems,
                    staleItems = staleContinueWatchingItems,
                    watchNextEpisodes = watchNextEpisodes,
                    staleEpisodes = staleEpisodes,
                ),
                ContinueWatchingState(
                    isRefreshing = false,
                    isGridMode = true,
                    labels = previewLabels,
                    watchNextItems = continueWatchingItems,
                    staleItems = staleContinueWatchingItems,
                    watchNextEpisodes = watchNextEpisodes,
                    staleEpisodes = staleEpisodes,
                ),
                ContinueWatchingState(
                    isGridMode = false,
                    isRefreshing = false,
                    labels = previewLabels,
                    watchNextItems = continueWatchingItems,
                    staleItems = staleContinueWatchingItems,
                    watchNextEpisodes = watchNextEpisodes,
                    staleEpisodes = staleEpisodes,
                ),
                ContinueWatchingState(
                    isRefreshing = false,
                    labels = previewLabels,
                    watchNextItems = continueWatchingItems,
                    staleItems = staleContinueWatchingItems,
                    watchNextEpisodes = watchNextEpisodes,
                    staleEpisodes = staleEpisodes,
                    message = UiMessage(message = "Something went Wrong"),
                ),
                ContinueWatchingState(labels = previewLabels),
                ContinueWatchingState(
                    labels = previewLabels,
                    message = UiMessage(message = "Something went Wrong"),
                ),
            )
        }
}
