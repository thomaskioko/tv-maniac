package com.thomaskioko.tvmaniac.watchlist.presenter

import com.thomaskioko.tvmaniac.core.view.UiMessage
import com.thomaskioko.tvmaniac.watchlist.presenter.model.WatchlistItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class WatchlistState(
    val query: String = "",
    val isSearchActive: Boolean = false,
    val isGridMode: Boolean = true,
    val isLoading: Boolean = false,
    val items: ImmutableList<WatchlistItem> = persistentListOf(),
    val message: UiMessage? = null,
)
