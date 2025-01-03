package com.thomaskioko.tvmaniac.presentation.watchlist

sealed interface WatchlistAction

data object ReloadWatchlist : WatchlistAction

data class WatchlistShowClicked(val id: Long) : WatchlistAction

data class WatchlistQueryChanged(val query: String) : WatchlistAction

data object ClearWatchlistQuery : WatchlistAction

data object ChangeListStyleClicked : WatchlistAction
