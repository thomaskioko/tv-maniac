package com.thomaskioko.tvmaniac.search.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.presenter.model.ShowGenre
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import kotlinx.collections.immutable.toImmutableList

internal class SearchPreviewParameterProvider : PreviewParameterProvider<SearchShowState> {
    override val values: Sequence<SearchShowState>
        get() {
            return sequenceOf(
                // Empty state (no results, not loading)
                SearchShowState(
                    query = "test query",
                    isUpdating = false,
                ),
                // Error state
                SearchShowState(
                    errorMessage = "Something went wrong",
                ),
                // Genre browsing mode
                SearchShowState(
                    genres = createGenreShowList(),
                ),
                // Search results
                SearchShowState(
                    query = "loki",
                    searchResults = createDiscoverShowList(),
                ),
                // Loading state
                SearchShowState(
                    isUpdating = true,
                ),
            )
        }
}

internal fun createDiscoverShowList(size: Int = 5) = List(size) { discoverShow }.toImmutableList()

internal val discoverShow = ShowItem(
    tmdbId = 84958,
    traktId = 84958,
    title = "Loki",
    posterImageUrl = null,
    overview = "After stealing the Tesseract during the events of Avengers: Endgame, an ",
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
