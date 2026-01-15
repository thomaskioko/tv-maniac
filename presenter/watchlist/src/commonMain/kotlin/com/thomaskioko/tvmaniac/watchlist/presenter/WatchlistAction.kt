package com.thomaskioko.tvmaniac.watchlist.presenter

public sealed interface WatchlistAction

public data class WatchlistShowClicked(val traktId: Long) : WatchlistAction

public data class WatchlistQueryChanged(val query: String) : WatchlistAction

public data object ClearWatchlistQuery : WatchlistAction

public data object ChangeListStyleClicked : WatchlistAction

public data class MessageShown(val id: Long) : WatchlistAction

public data class UpNextEpisodeClicked(val showTraktId: Long, val episodeId: Long) : WatchlistAction

public data class ShowTitleClicked(val showTraktId: Long) : WatchlistAction

public data class MarkUpNextEpisodeWatched(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : WatchlistAction

public data class UnfollowShowFromUpNext(val showTraktId: Long) : WatchlistAction

public data class OpenSeasonFromUpNext(
    val showTraktId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : WatchlistAction
