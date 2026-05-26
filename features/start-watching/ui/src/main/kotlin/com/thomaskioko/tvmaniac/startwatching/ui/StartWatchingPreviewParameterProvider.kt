package com.thomaskioko.tvmaniac.startwatching.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.startwatching.presenter.StartWatchingState
import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal val previewStartWatchingItems: ImmutableList<StartWatchingItem> = persistentListOf(
    StartWatchingItem(
        traktId = 1,
        title = "Breaking Bad",
        posterImageUrl = null,
        year = "2008",
        episodeId = 11,
        episodeTitle = "Pilot",
        episodeNumberFormatted = "S01 | E01",
        seasonNumber = 1,
        episodeNumber = 1,
        runtime = "58 min",
    ),
    StartWatchingItem(
        traktId = 2,
        title = "Better Call Saul",
        posterImageUrl = null,
        year = "2015",
        episodeId = 21,
        episodeTitle = "Uno",
        episodeNumberFormatted = "S01 | E01",
        seasonNumber = 1,
        episodeNumber = 1,
        runtime = "53 min",
    ),
    StartWatchingItem(traktId = 3, title = "Severance", posterImageUrl = null, year = "2022"),
)

internal class StartWatchingPreviewParameterProvider : PreviewParameterProvider<StartWatchingState> {
    override val values: Sequence<StartWatchingState>
        get() = sequenceOf(
            StartWatchingState(items = previewStartWatchingItems, isGridMode = false),
            StartWatchingState(items = previewStartWatchingItems, isGridMode = true),
            StartWatchingState(),
        )
}
