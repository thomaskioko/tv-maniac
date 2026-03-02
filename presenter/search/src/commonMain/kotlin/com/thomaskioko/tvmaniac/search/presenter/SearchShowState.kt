package com.thomaskioko.tvmaniac.search.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.genre.model.GenreShowCategory
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.BrowsingGenres
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.Error
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.InitialLoading
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.SearchEmpty
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.SearchLoading
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.SearchResults
import com.thomaskioko.tvmaniac.search.presenter.model.CategoryItem
import com.thomaskioko.tvmaniac.search.presenter.model.GenreRowModel
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public sealed interface SearchUiState {
    public data object InitialLoading : SearchUiState
    public data class BrowsingGenres(
        val genreRows: ImmutableList<GenreRowModel>,
        val selectedCategory: GenreShowCategory,
        val categoryTitle: String,
        val categories: ImmutableList<CategoryItem>,
        val isRefreshing: Boolean,
    ) : SearchUiState
    public data class SearchResults(
        val results: ImmutableList<ShowItem>,
        val isUpdating: Boolean,
    ) : SearchUiState
    public data object SearchLoading : SearchUiState
    public data object SearchEmpty : SearchUiState
    public data class Error(val message: String) : SearchUiState
}

public data class SearchShowState(
    val query: String = "",
    val isUpdating: Boolean = false,
    val isRefreshing: Boolean = true,
    val message: UiMessage? = null,
    val searchResults: ImmutableList<ShowItem> = persistentListOf(),
    val genreRows: ImmutableList<GenreRowModel> = persistentListOf(),
    val selectedCategory: GenreShowCategory = GenreShowCategory.POPULAR,
    val categoryTitle: String = "",
    val categories: ImmutableList<CategoryItem> = persistentListOf(),
) {
    public val uiState: SearchUiState
        get() = when {
            message != null && genreRows.isEmpty() -> Error(message.message)
            isRefreshing && genreRows.isEmpty() -> InitialLoading
            query.isNotEmpty() && searchResults.isNotEmpty() -> SearchResults(searchResults, isUpdating)
            query.isNotEmpty() && isUpdating -> SearchLoading
            query.isNotEmpty() && searchResults.isEmpty() -> SearchEmpty
            genreRows.isNotEmpty() -> BrowsingGenres(genreRows, selectedCategory, categoryTitle, categories, isRefreshing)
            else -> InitialLoading
        }

    public companion object {
        public val Empty: SearchShowState = SearchShowState()
    }
}
