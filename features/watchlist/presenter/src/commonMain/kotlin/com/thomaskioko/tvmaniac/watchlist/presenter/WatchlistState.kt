package com.thomaskioko.tvmaniac.watchlist.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.watchlist.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class WatchlistState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val isGridMode: Boolean = true,
    val isRefreshing: Boolean = true,
    val isSyncing: Boolean = false,
    val watchNextItems: ImmutableList<WatchlistItem> = persistentListOf(),
    val staleItems: ImmutableList<WatchlistItem> = persistentListOf(),
    val watchNextEpisodes: ImmutableList<UpNextEpisodeItem> = persistentListOf(),
    val staleEpisodes: ImmutableList<UpNextEpisodeItem> = persistentListOf(),
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = watchNextItems.isEmpty() && staleItems.isEmpty() &&
            watchNextEpisodes.isEmpty() && staleEpisodes.isEmpty()

    val showLoading: Boolean
        get() = isRefreshing && isEmpty
}
