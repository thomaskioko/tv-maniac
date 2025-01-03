package com.thomaskioko.tvmaniac.presentation.watchlist

import com.thomaskioko.tvmaniac.presentation.watchlist.model.WatchlistItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface WatchlistState {
    val query: String?
    val isSearchActive: Boolean
    val isGridMode: Boolean
}

data object LoadingShows : WatchlistState {
    override val query: String? = null
    override val isSearchActive: Boolean = false
    override val isGridMode: Boolean = true
}

data class WatchlistContent(
  override val query: String? = null,
  override val isSearchActive: Boolean = false,
  override val isGridMode: Boolean = true,
  val list: ImmutableList<WatchlistItem> = persistentListOf(),
) : WatchlistState

data class EmptyWatchlist(
    override val query: String? = null,
    override val isSearchActive: Boolean = false,
    override val isGridMode: Boolean = true,
    val message: String? = null
) : WatchlistState
