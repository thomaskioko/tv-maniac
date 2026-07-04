package com.thomaskioko.tvmaniac.discover.presenter.upnext

public sealed interface DiscoverUpNextAction

public data class DiscoverEpisodeLongPressed(val showId: Long, val episodeId: Long) : DiscoverUpNextAction
