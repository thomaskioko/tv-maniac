package com.thomaskioko.tvmaniac.search.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.search.presenter.EmptySearchResult
import com.thomaskioko.tvmaniac.search.presenter.SearchResultAvailable
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.presenter.ShowContentAvailable
import com.thomaskioko.tvmaniac.search.presenter.model.ShowGenre
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
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
    posterImageUrl = null,
    overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” an ",
    status = "Ended",
    inLibrary = false,
)

internal fun createGenreShowList(size: Int = 5) = List(size) {
    ShowGenre(
        id = 84958,
        name = "Horror",
        posterUrl = null,
    )
}.toImmutableList()
