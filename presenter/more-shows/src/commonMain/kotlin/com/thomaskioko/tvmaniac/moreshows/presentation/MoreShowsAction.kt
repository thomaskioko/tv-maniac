package com.thomaskioko.tvmaniac.moreshows.presentation

public sealed interface MoreShowsActions

public data object MoreBackClicked : MoreShowsActions

public data object RefreshMoreShows : MoreShowsActions

public data class MoreShowClicked(val showId: Long) : MoreShowsActions
