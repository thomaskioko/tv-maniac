package com.thomaskioko.tvmaniac.search.presenter

import com.thomaskioko.tvmaniac.search.presenter.model.ShowGenre
import com.thomaskioko.tvmaniac.search.presenter.model.ShowItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class SearchShowState(
    val query: String = "",
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val searchResults: ImmutableList<ShowItem> = persistentListOf(),
    val genres: ImmutableList<ShowGenre> = persistentListOf(),
) {
    public companion object {
        public val Empty: SearchShowState = SearchShowState()
    }
}
