package com.thomaskioko.tvmaniac.myshows.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.myshows.presenter.model.MyShowsItem
import com.thomaskioko.tvmaniac.myshows.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

public data class MyShowsState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val isGridMode: Boolean = true,
    val isRefreshing: Boolean = false,
    val isSyncing: Boolean = false,
    val sortOption: WatchlistSortOption = WatchlistSortOption.ADDED_DESC,
    val emptyStateText: String = "",
    val watchNextItems: ImmutableList<MyShowsItem> = persistentListOf(),
    val staleItems: ImmutableList<MyShowsItem> = persistentListOf(),
    val watchNextEpisodes: ImmutableList<UpNextEpisodeItem> = persistentListOf(),
    val staleEpisodes: ImmutableList<UpNextEpisodeItem> = persistentListOf(),
    val updatingEpisodeIds: ImmutableSet<Long> = persistentSetOf(),
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = watchNextItems.isEmpty() && staleItems.isEmpty() &&
            watchNextEpisodes.isEmpty() && staleEpisodes.isEmpty()

    val showLoading: Boolean
        get() = isSyncing && isEmpty
}
