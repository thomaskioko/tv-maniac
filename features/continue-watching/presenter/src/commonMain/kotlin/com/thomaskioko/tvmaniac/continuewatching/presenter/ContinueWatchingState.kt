package com.thomaskioko.tvmaniac.continuewatching.presenter

import com.thomaskioko.tvmaniac.continuewatching.presenter.model.ContinueWatchingItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.StartWatchingItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.watchlistprefs.api.model.WatchlistSortOption
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

public data class ContinueWatchingState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val isGridMode: Boolean = true,
    val isRefreshing: Boolean = false,
    val isSyncing: Boolean = false,
    val sortOption: WatchlistSortOption = WatchlistSortOption.ADDED_DESC,
    val emptyStateText: String = "",
    val startWatchingTitle: String = "",
    val continueWatchingTitle: String = "",
    val startWatchingItems: ImmutableList<StartWatchingItem> = persistentListOf(),
    val watchNextItems: ImmutableList<ContinueWatchingItem> = persistentListOf(),
    val staleItems: ImmutableList<ContinueWatchingItem> = persistentListOf(),
    val watchNextEpisodes: ImmutableList<UpNextEpisodeItem> = persistentListOf(),
    val staleEpisodes: ImmutableList<UpNextEpisodeItem> = persistentListOf(),
    val updatingEpisodeIds: ImmutableSet<Long> = persistentSetOf(),
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = startWatchingItems.isEmpty() && watchNextItems.isEmpty() && staleItems.isEmpty() &&
            watchNextEpisodes.isEmpty() && staleEpisodes.isEmpty()

    val showLoading: Boolean
        get() = isSyncing && isEmpty
}
