package com.thomaskioko.tvmaniac.ui.discover

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverViewState
import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import kotlinx.collections.immutable.toImmutableList

val discoverShow =
  DiscoverShow(
    tmdbId = 84958,
    title = "Loki",
    posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
    overView = "After stealing the Tesseract during the events of “Avengers: Endgame,” an ",
  )

val discoverContentSuccess =
  DiscoverViewState(
    featuredShows = createDiscoverShowList(5),
    topRatedShows = createDiscoverShowList(),
    popularShows = createDiscoverShowList(),
    upcomingShows = createDiscoverShowList(),
  )

private fun createDiscoverShowList(size: Int = 20) = List(size) { discoverShow }.toImmutableList()

class DiscoverPreviewParameterProvider : PreviewParameterProvider<DiscoverViewState> {
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
