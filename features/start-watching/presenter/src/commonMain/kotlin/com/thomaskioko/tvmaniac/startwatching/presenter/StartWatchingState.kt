package com.thomaskioko.tvmaniac.startwatching.presenter

import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class StartWatchingState(
    val isLoading: Boolean = false,
    val isGridMode: Boolean = true,
    val items: ImmutableList<StartWatchingItem> = persistentListOf(),
) {
    val isEmpty: Boolean
        get() = !isLoading && items.isEmpty()
}
