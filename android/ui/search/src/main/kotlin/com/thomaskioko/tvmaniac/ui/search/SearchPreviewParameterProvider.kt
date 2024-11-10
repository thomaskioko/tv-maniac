package com.thomaskioko.tvmaniac.ui.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.search.EmptySearchState
import com.thomaskioko.tvmaniac.presentation.search.ErrorSearchState
import com.thomaskioko.tvmaniac.presentation.search.ShowItem
import com.thomaskioko.tvmaniac.presentation.search.SearchResultAvailable
import com.thomaskioko.tvmaniac.presentation.search.SearchShowState
import com.thomaskioko.tvmaniac.presentation.search.ShowContentAvailable
import kotlinx.collections.immutable.toImmutableList

class SearchPreviewParameterProvider : PreviewParameterProvider<SearchShowState> {
  override val values: Sequence<SearchShowState>
    get()  {
      return sequenceOf(
        EmptySearchState(),
        ErrorSearchState(errorMessage = "Something went wrong"),
        ShowContentAvailable(
          featuredShows = createDiscoverShowList(),
          trendingShows = createDiscoverShowList(),
          upcomingShows = createDiscoverShowList(),
        ),
        SearchResultAvailable(
          results = createDiscoverShowList(),
        )
      )
    }
}

internal fun createDiscoverShowList(size: Int = 5) = List(size) { discoverShow }.toImmutableList()

val discoverShow = ShowItem(
  tmdbId = 84958,
  title = "Loki",
  posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
  overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” an ",
  status = "Ended",
  inLibrary = false,
)
