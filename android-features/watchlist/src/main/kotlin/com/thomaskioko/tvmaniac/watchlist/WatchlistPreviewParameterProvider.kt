package com.thomaskioko.tvmaniac.watchlist

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.watchlist.ErrorLoadingShows
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistContent
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistItem
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistState

val list = List(6) {
    WatchlistItem(
        traktId = 84958,
        tmdbId = 84958,
        title = "Loki",
        posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    )
}

class FollowingPreviewParameterProvider : PreviewParameterProvider<WatchlistState> {
    override val values: Sequence<WatchlistState>
        get() {
            return sequenceOf(
                WatchlistContent(list = list),
                ErrorLoadingShows(message = "Something went Wrong"),
            )
        }
}
