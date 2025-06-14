package com.thomaskioko.tvmaniac.moreshows.presentation

sealed interface MoreShowsActions

data object MoreBackClicked : MoreShowsActions

data object RefreshMoreShows : MoreShowsActions

data class MoreShowClicked(val showId: Long) : MoreShowsActions
