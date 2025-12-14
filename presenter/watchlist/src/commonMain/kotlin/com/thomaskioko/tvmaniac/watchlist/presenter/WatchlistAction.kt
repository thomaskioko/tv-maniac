package com.thomaskioko.tvmaniac.watchlist.presenter

sealed interface WatchlistAction

data object ReloadWatchlist : WatchlistAction

data class WatchlistShowClicked(val id: Long) : WatchlistAction

data class WatchlistQueryChanged(val query: String) : WatchlistAction

data object ClearWatchlistQuery : WatchlistAction

data object ChangeListStyleClicked : WatchlistAction

data class MessageShown(val id: Long) : WatchlistAction

data class UpNextEpisodeClicked(val showId: Long, val episodeId: Long) : WatchlistAction

data class ShowTitleClicked(val showId: Long) : WatchlistAction

data class MarkUpNextEpisodeWatched(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : WatchlistAction

data class UnfollowShowFromUpNext(val showId: Long) : WatchlistAction

data class OpenSeasonFromUpNext(
    val showId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : WatchlistAction
