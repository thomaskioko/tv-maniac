package com.thomaskioko.tvmaniac.startwatching.presenter

public sealed interface StartWatchingAction

public data class StartWatchingShowClicked(val traktId: Long) : StartWatchingAction
