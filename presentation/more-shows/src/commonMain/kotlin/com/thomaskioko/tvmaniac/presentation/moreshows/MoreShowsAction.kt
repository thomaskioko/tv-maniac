package com.thomaskioko.tvmaniac.presentation.moreshows

sealed interface MoreShowsActions

data object MoreBackClicked : MoreShowsActions

data class ShowClicked(val showId: Long) : MoreShowsActions
