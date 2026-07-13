package com.thomaskioko.tvmaniac.discover.presenter.catalog

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class DiscoverCatalogState(
    val isInitial: Boolean = true,
    val loading: Boolean = false,
    val trendingShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val upcomingShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val popularShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val topRatedShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val trendingTitle: String = "",
    val upcomingTitle: String = "",
    val popularTitle: String = "",
    val topRatedTitle: String = "",
    val trendingVisible: Boolean = true,
    val upcomingVisible: Boolean = true,
    val popularVisible: Boolean = true,
    val topRatedVisible: Boolean = true,
    val message: UiMessage? = null,
) {
    val isRefreshing: Boolean
        get() = isInitial || loading

    val isEmpty: Boolean
        get() = trendingShows.isEmpty() && upcomingShows.isEmpty() &&
            popularShows.isEmpty() && topRatedShows.isEmpty()
}
