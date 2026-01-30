package com.thomaskioko.tvmaniac.search.presenter

import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.BrowsingGenres
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.Error
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.InitialLoading
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.SearchEmpty
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.SearchLoading
import com.thomaskioko.tvmaniac.search.presenter.SearchUiState.SearchResults
import com.thomaskioko.tvmaniac.search.presenter.model.ShowGenre
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public sealed interface SearchUiState {
    public data object InitialLoading : SearchUiState
    public data class BrowsingGenres(
        val genres: ImmutableList<ShowGenre>,
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
    val isUpdating: Boolean = true,
    val errorMessage: String? = null,
    val searchResults: ImmutableList<ShowItem> = persistentListOf(),
    val genres: ImmutableList<ShowGenre> = persistentListOf(),
) {
    public val uiState: SearchUiState
        get() = when {
            errorMessage != null && genres.isEmpty() -> Error(errorMessage)
            isUpdating && searchResults.isEmpty() && genres.isEmpty() -> InitialLoading
            query.isNotEmpty() && searchResults.isNotEmpty() -> SearchResults(searchResults, isUpdating)
            query.isNotEmpty() && isUpdating -> SearchLoading
            query.isNotEmpty() && searchResults.isEmpty() -> SearchEmpty
            genres.isNotEmpty() -> BrowsingGenres(genres, isUpdating)
            else -> InitialLoading
        }

    public companion object {
        public val Empty: SearchShowState = SearchShowState()
    }
}
