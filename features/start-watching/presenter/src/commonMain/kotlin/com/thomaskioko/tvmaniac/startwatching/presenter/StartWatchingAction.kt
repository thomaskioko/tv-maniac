package com.thomaskioko.tvmaniac.startwatching.presenter

public sealed interface StartWatchingAction

public data class StartWatchingShowClicked(val showId: Long) : StartWatchingAction

public data class StartWatchingMessageShown(val id: Long) : StartWatchingAction

public data class RefreshStartWatching(val forceRefresh: Boolean = true) : StartWatchingAction
