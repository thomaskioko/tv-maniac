package com.thomaskioko.tvmaniac.startwatching.presenter

public sealed interface StartWatchingAction

public data class StartWatchingShowClicked(val traktId: Long) : StartWatchingAction

public data class StartWatchingEpisodeClicked(
    val showTraktId: Long,
    val episodeId: Long,
) : StartWatchingAction

public data class StartWatchingShowTitleClicked(val showTraktId: Long) : StartWatchingAction

public data class MarkStartWatchingEpisodeWatched(
    val showTraktId: Long,
    val episodeId: Long,
    val seasonNumber: Long,
    val episodeNumber: Long,
) : StartWatchingAction

public data class StartWatchingMessageShown(val id: Long) : StartWatchingAction

public data class RefreshStartWatching(val forceRefresh: Boolean = true) : StartWatchingAction
