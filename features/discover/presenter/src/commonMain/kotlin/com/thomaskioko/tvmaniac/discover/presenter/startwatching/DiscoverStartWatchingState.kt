package com.thomaskioko.tvmaniac.discover.presenter.startwatching

import com.thomaskioko.tvmaniac.discover.presenter.model.DiscoverShow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class DiscoverStartWatchingState(
    val startWatchingShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val startWatchingTitle: String = "",
    val startWatchingVisible: Boolean = true,
)
