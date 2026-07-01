package com.thomaskioko.tvmaniac.myshows.presenter

import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption

public data class MyShowsState(
    val selectedPage: Int = 0,
    val continueWatchingTitle: String = "",
    val startWatchingTitle: String = "",
    val query: String = "",
    val isSearchActive: Boolean = false,
    val isGridMode: Boolean = true,
    val sortOption: WatchlistSortOption = WatchlistSortOption.ADDED_DESC,
    val showRefreshIndicator: Boolean = false,
)
