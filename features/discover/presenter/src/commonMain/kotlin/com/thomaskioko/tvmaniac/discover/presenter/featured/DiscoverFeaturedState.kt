package com.thomaskioko.tvmaniac.discover.presenter.featured

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class DiscoverFeaturedState(
    val isInitial: Boolean = true,
    val loading: Boolean = false,
    val featuredShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val message: UiMessage? = null,
) {
    val isRefreshing: Boolean
        get() = isInitial || loading

    val isEmpty: Boolean
        get() = featuredShows.isEmpty()
}
