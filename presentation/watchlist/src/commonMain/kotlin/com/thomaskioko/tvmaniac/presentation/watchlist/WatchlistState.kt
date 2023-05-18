package com.thomaskioko.tvmaniac.presentation.watchlist

sealed interface WatchlistState

object LoadingShows : WatchlistState

data class WatchlistContent(
    val list: List<WatchlistItem> = emptyList(),
) : WatchlistState

data class ErrorLoadingShows(val message: String) : WatchlistState
