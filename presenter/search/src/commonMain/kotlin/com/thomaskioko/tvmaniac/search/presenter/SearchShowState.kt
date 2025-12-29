package com.thomaskioko.tvmaniac.search.presenter

import com.thomaskioko.tvmaniac.search.presenter.model.ShowGenre
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public sealed interface SearchShowState {
    public val query: String?
    public val isUpdating: Boolean
}

public data class InitialSearchState(
    override val query: String? = null,
    override val isUpdating: Boolean = true,
) : SearchShowState

public data class EmptySearchResult(
    override val query: String? = null,
    override val isUpdating: Boolean = false,
    val errorMessage: String? = null,
) : SearchShowState

public data class SearchResultAvailable(
    override val query: String? = null,
    override val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val results: ImmutableList<ShowItem> = persistentListOf(),
) : SearchShowState

public data class ShowContentAvailable(
    override val query: String? = null,
    override val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val genres: ImmutableList<ShowGenre> = persistentListOf(),
) : SearchShowState
