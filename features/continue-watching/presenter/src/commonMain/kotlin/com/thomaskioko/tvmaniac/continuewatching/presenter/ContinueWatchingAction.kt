package com.thomaskioko.tvmaniac.continuewatching.presenter

public sealed interface ContinueWatchingAction

public data class ContinueWatchingShowClicked(val traktId: Long) : ContinueWatchingAction

public data class ContinueWatchingMessageShown(val id: Long) : ContinueWatchingAction

public data class UpNextEpisodeClicked(val showTraktId: Long, val episodeId: Long) : ContinueWatchingAction

public data class ShowTitleClicked(val showTraktId: Long) : ContinueWatchingAction

public data class MarkUpNextEpisodeWatched(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : ContinueWatchingAction

public data class UnfollowShowFromUpNext(val showTraktId: Long) : ContinueWatchingAction

public data class OpenSeasonFromUpNext(
    val showTraktId: Long,
    val seasonId: Long,
    val seasonNumber: Long,
) : ContinueWatchingAction

public data class RefreshContinueWatching(val forceRefresh: Boolean = false) : ContinueWatchingAction
