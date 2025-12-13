package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistState
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

internal val watchlistItems = List(6) {
    WatchlistItem(
        tmdbId = 84958,
        title = "Loki",
        posterImageUrl = null,
        year = "2021",
        status = "Canceled",
        seasonCount = 6,
        episodeCount = 12,
    )
}
    .toPersistentList()

class WatchlistPreviewParameterProvider : PreviewParameterProvider<WatchlistState> {
    override val values: Sequence<WatchlistState>
        get() {
            return sequenceOf(
                WatchlistState(
                    isGridMode = false,
                    items = watchlistItems,
                ),
                WatchlistState(items = watchlistItems),
                WatchlistState(
                    items = watchlistItems,
                    message = UiMessage(message = "Something went Wrong"),
                ),
                WatchlistState(items = persistentListOf()),
                WatchlistState(
                    items = persistentListOf(),
                    message = UiMessage(message = "Something went Wrong"),
                ),
            )
        }
}
