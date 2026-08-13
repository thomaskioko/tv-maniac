package com.thomaskioko.tvmaniac.continuewatching.presenter

import com.thomaskioko.tvmaniac.continuewatching.presenter.model.ContinueWatchingItem
import com.thomaskioko.tvmaniac.continuewatching.presenter.model.UpNextEpisodeItem
import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf

public data class ContinueWatchingState(
    val query: String = "",
    val listStyle: ListStyle = ListStyle.GRID,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isSyncing: Boolean = false,
    val labels: ContinueWatchingLabels = ContinueWatchingLabels(),
    val watchNextItems: ImmutableList<ContinueWatchingItem> = persistentListOf(),
    val staleItems: ImmutableList<ContinueWatchingItem> = persistentListOf(),
    val watchNextEpisodes: ImmutableList<UpNextEpisodeItem> = persistentListOf(),
    val staleEpisodes: ImmutableList<UpNextEpisodeItem> = persistentListOf(),
    val updatingEpisodeIds: ImmutableSet<Long> = persistentSetOf(),
    val isUpdating: Boolean = false,
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = watchNextItems.isEmpty() && staleItems.isEmpty() &&
            watchNextEpisodes.isEmpty() && staleEpisodes.isEmpty()

    val showLoading: Boolean
        get() = (isLoading || isSyncing) && isEmpty && query.isBlank()

    val showRefreshIndicator: Boolean
        get() = (isLoading || isSyncing || isRefreshing) && !isEmpty
}
