package com.thomaskioko.tvmaniac.startwatching.presenter

import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class StartWatchingState(
    val isSyncing: Boolean = false,
    val isGridMode: Boolean = true,
    val items: ImmutableList<StartWatchingItem> = persistentListOf(),
) {
    val isEmpty: Boolean
        get() = items.isEmpty()

    val showLoading: Boolean
        get() = isSyncing && isEmpty
}
