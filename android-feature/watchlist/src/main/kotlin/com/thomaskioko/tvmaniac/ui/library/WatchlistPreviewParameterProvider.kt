package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.watchlist.EmptyWatchlist
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistContent
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistState
import com.thomaskioko.tvmaniac.presentation.watchlist.model.WatchlistItem
import kotlinx.collections.immutable.toPersistentList

val list =
  List(6) {
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
