package com.thomaskioko.tvmaniac.presentation.watchlist

sealed interface LibraryAction

data object ReloadLibrary : LibraryAction
data class ShowClicked(val id: Long) : LibraryAction
