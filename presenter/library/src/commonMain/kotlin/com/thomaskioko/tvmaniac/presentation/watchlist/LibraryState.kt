package com.thomaskioko.tvmaniac.presentation.watchlist

import com.thomaskioko.tvmaniac.presentation.watchlist.model.WatchlistItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface LibraryState {
    val query: String?
    val isSearchActive: Boolean
    val isGridMode: Boolean
}

data object LoadingShows : LibraryState {
    override val query: String? = null
    override val isSearchActive: Boolean = false
    override val isGridMode: Boolean = true
}

data class LibraryContent(
  override val query: String? = null,
  override val isSearchActive: Boolean = false,
  override val isGridMode: Boolean = true,
  val list: ImmutableList<WatchlistItem> = persistentListOf(),
) : LibraryState

data class EmptyWatchlist(
    override val query: String? = null,
    override val isSearchActive: Boolean = false,
    override val isGridMode: Boolean = true,
    val message: String? = null
) : LibraryState
