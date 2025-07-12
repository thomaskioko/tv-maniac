package com.thomaskioko.tvmaniac.watchlist.presenter

sealed interface WatchlistAction

data object ReloadWatchlist : WatchlistAction

data class WatchlistShowClicked(val id: Long) : WatchlistAction

data class WatchlistQueryChanged(val query: String) : WatchlistAction

data object ClearWatchlistQuery : WatchlistAction

data object ChangeListStyleClicked : WatchlistAction

data class MessageShown(val id: Long) : WatchlistAction
