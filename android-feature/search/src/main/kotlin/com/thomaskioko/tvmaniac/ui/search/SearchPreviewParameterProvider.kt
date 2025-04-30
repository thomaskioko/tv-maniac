package com.thomaskioko.tvmaniac.ui.search

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.presentation.search.EmptySearchResult
import com.thomaskioko.tvmaniac.presentation.search.SearchResultAvailable
import com.thomaskioko.tvmaniac.presentation.search.SearchShowState
import com.thomaskioko.tvmaniac.presentation.search.ShowContentAvailable
import com.thomaskioko.tvmaniac.presentation.search.model.ShowGenre
import com.thomaskioko.tvmaniac.presentation.search.model.ShowItem
import kotlinx.collections.immutable.toImmutableList

class SearchPreviewParameterProvider : PreviewParameterProvider<SearchShowState> {
  override val values: Sequence<SearchShowState>
    get() {
      return sequenceOf(
        EmptySearchResult(),
        EmptySearchResult(errorMessage = "Something went wrong"),
        ShowContentAvailable(
          genres = createGenreShowList(),
        ),
        SearchResultAvailable(
          results = createDiscoverShowList(),
        ),
      )
    }
}

internal fun createDiscoverShowList(size: Int = 5) = List(size) { discoverShow }.toImmutableList()

internal val discoverShow = ShowItem(
  tmdbId = 84958,
  title = "Loki",
  posterImageUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
  overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” an ",
  status = "Ended",
  inLibrary = false,
)

internal fun createGenreShowList(size: Int = 5) = List(size) {
  ShowGenre(
    id = 84958,
    name = "Horror",
    posterUrl = "/kEl2t3OhXc3Zb9FBh1AuYzRTgZp.jpg",
  )
}.toImmutableList()
