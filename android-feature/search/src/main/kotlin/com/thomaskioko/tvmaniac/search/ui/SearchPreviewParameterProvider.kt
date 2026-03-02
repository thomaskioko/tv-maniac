package com.thomaskioko.tvmaniac.search.ui

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.search.presenter.SearchShowState
import com.thomaskioko.tvmaniac.search.presenter.model.CategoryItem
import com.thomaskioko.tvmaniac.search.presenter.model.GenreRowModel
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import kotlinx.collections.immutable.toImmutableList

internal class SearchPreviewParameterProvider : PreviewParameterProvider<SearchShowState> {
    override val values: Sequence<SearchShowState>
        get() {
            return sequenceOf(
                SearchShowState(
                    query = "test query",
                    isRefreshing = false,
                ),
                SearchShowState(
                    message = UiMessage(message = "Oops! Something went wrong!"),
                ),
                SearchShowState(
                    isRefreshing = false,
                    genreRows = createGenreRowList(),
                    selectedCategory = GenreShowCategory.POPULAR,
                    categoryTitle = "Category",
                    categories = previewCategories(),
                ),
                SearchShowState(
                    query = "loki",
                    isRefreshing = false,
                    searchResults = createDiscoverShowList(),
                ),
                SearchShowState(
                    isRefreshing = true,
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

internal fun createGenreRowList() = listOf(
    GenreRowModel(
        slug = "action",
        name = "Action",
        subtitle = "Non-stop thrill and action",
        shows = List(5) {
            ShowItem(
                traktId = 84958L + it,
                tmdbId = 84958L + it,
                title = "Loki",
                posterImageUrl = null,
                inLibrary = false,
            )
        }.toImmutableList(),
    ),
    GenreRowModel(
        slug = "comedy",
        name = "Comedy",
        subtitle = "Guaranteed laughs in every episode",
        shows = List(5) {
            ShowItem(
                traktId = 94958L + it,
                tmdbId = 94958L + it,
                title = "Ted Lasso",
                posterImageUrl = null,
                inLibrary = false,
            )
        }.toImmutableList(),
    ),
).toImmutableList()

internal fun previewCategories() = listOf(
    CategoryItem(GenreShowCategory.POPULAR, "Popular"),
    CategoryItem(GenreShowCategory.TRENDING, "Trending"),
    CategoryItem(GenreShowCategory.TOP_RATED, "Top Rated"),
    CategoryItem(GenreShowCategory.MOST_WATCHED, "Most Watched"),
).toImmutableList()
