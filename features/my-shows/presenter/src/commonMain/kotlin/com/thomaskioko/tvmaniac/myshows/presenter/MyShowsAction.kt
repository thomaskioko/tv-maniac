package com.thomaskioko.tvmaniac.myshows.presenter

import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption

public sealed interface MyShowsAction {
    public data class SelectPage(val index: Int) : MyShowsAction

    public data class QueryChanged(val query: String) : MyShowsAction

    public data object ClearQuery : MyShowsAction

    public data object ToggleSearch : MyShowsAction

    public data class ChangeListStyle(val isGridMode: Boolean) : MyShowsAction

    public data class ChangeSortOption(val sortOption: WatchlistSortOption) : MyShowsAction
}
