package com.thomaskioko.tvmaniac.discover.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import com.thomaskioko.tvmaniac.discover.presenter.model.NextEpisodeUiModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DiscoverViewState(
    val featuredRefreshing: Boolean = false,
    val topRatedRefreshing: Boolean = false,
    val trendingRefreshing: Boolean = false,
    val upcomingRefreshing: Boolean = false,
    val popularRefreshing: Boolean = false,
    val nextEpisodesRefreshing: Boolean = false,
    val upNextRefreshing: Boolean = false,
    val featuredShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val topRatedShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val popularShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val upcomingShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val trendingToday: ImmutableList<DiscoverShow> = persistentListOf(),
    val nextEpisodes: ImmutableList<NextEpisodeUiModel> = persistentListOf(),
    val message: UiMessage? = null,
) {
    val isRefreshing: Boolean
        get() = featuredRefreshing || topRatedRefreshing || trendingRefreshing ||
            popularRefreshing || upcomingRefreshing || nextEpisodesRefreshing || upNextRefreshing

    val isEmpty: Boolean
        get() = !isRefreshing && isEmpty(featuredShows, topRatedShows, popularShows, upcomingShows, trendingToday)

    val showError: Boolean
        get() = message != null && isEmpty

    val showSnackBarError: Boolean
        get() = message != null && !isEmpty && !isRefreshing

    private fun isEmpty(vararg responses: List<DiscoverShow>?): Boolean {
        return responses.all { it.isNullOrEmpty() }
    }

    companion object {
        val Empty = DiscoverViewState()
    }
}
