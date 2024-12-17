package com.thomaskioko.tvmaniac.ui.library

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.watchlist.EmptyWatchlist
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryContent
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryState
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

class LibraryPreviewParameterProvider : PreviewParameterProvider<LibraryState> {
  override val values: Sequence<LibraryState>
    get() {
      return sequenceOf(
        LibraryContent(list = list),
        EmptyWatchlist(message = "Something went Wrong"),
      )
    }
}
