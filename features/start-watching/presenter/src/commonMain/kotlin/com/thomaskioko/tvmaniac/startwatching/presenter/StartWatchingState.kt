package com.thomaskioko.tvmaniac.startwatching.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

public data class StartWatchingState(
    val isSyncing: Boolean = false,
    val isRefreshing: Boolean = false,
    val items: ImmutableList<StartWatchingItem> = persistentListOf(),
    val message: UiMessage? = null,
) {
    val isEmpty: Boolean
        get() = items.isEmpty()

    val showLoading: Boolean
        get() = isSyncing && isEmpty
}
