package com.thomaskioko.tvmaniac.watchlist.presenter

public sealed interface WatchlistAction

public data object ReloadWatchlist : WatchlistAction

public data class WatchlistShowClicked(val id: Long) : WatchlistAction

public data class WatchlistQueryChanged(val query: String) : WatchlistAction

public data object ClearWatchlistQuery : WatchlistAction

public data object ChangeListStyleClicked : WatchlistAction

public data class MessageShown(val id: Long) : WatchlistAction

public data class UpNextEpisodeClicked(val showId: Long, val episodeId: Long) : WatchlistAction

public data class ShowTitleClicked(val showId: Long) : WatchlistAction

public data class MarkUpNextEpisodeWatched(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : WatchlistAction

public data class UnfollowShowFromUpNext(val showId: Long) : WatchlistAction

public data class OpenSeasonFromUpNext(
    val showId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : WatchlistAction
