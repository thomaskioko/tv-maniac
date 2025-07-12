package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.watchlist.presenter.EmptyWatchlist
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistContent
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistState
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.toPersistentList

val list = List(6) {
    WatchlistItem(
        tmdbId = 84958,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    )
}
    .toPersistentList()

class WatchlistPreviewParameterProvider : PreviewParameterProvider<WatchlistState> {
    override val values: Sequence<WatchlistState>
        get() {
            return sequenceOf(
                WatchlistContent(list = list),
                EmptyWatchlist(message = "Something went Wrong"),
            )
        }
}
