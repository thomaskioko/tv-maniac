package com.thomaskioko.tvmaniac.discover.presenter.startwatching

public sealed interface DiscoverStartWatchingAction

public data class StartWatchingItemClicked(val showId: Long) : DiscoverStartWatchingAction

public data object StartWatchingMoreClicked : DiscoverStartWatchingAction
