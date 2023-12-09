package com.thomaskioko.tvmaniac.presentation.moreshows

sealed interface MoreShowsActions

data object BackClicked : MoreShowsActions
data object RetryClicked : MoreShowsActions
data class ReloadShows(val category: Long) : MoreShowsActions
data class ShowClicked(val category: Long) : MoreShowsActions
