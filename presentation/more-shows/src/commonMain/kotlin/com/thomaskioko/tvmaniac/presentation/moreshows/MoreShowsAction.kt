package com.thomaskioko.tvmaniac.presentation.moreshows

sealed interface MoreShowsActions

data object MoreBackClicked : MoreShowsActions

data class MoreShowClicked(val showId: Long) : MoreShowsActions
