package com.thomaskioko.tvmaniac.continuewatching.presenter

public sealed interface ContinueWatchingAction

public data class ContinueWatchingShowClicked(val showId: Long) : ContinueWatchingAction

public data class ContinueWatchingMessageShown(val id: Long) : ContinueWatchingAction

public data class UpNextEpisodeClicked(val showId: Long, val episodeId: Long) : ContinueWatchingAction

public data class ShowTitleClicked(val showId: Long) : ContinueWatchingAction

public data class MarkUpNextEpisodeWatched(
    val showId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : ContinueWatchingAction

public data class UnfollowShowFromUpNext(val showId: Long) : ContinueWatchingAction

public data class OpenSeasonFromUpNext(
    val showId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : ContinueWatchingAction

public data class RefreshContinueWatching(val forceRefresh: Boolean = false) : ContinueWatchingAction
