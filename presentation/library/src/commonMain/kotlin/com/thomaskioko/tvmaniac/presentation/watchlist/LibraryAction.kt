package com.thomaskioko.tvmaniac.presentation.watchlist

sealed interface LibraryAction

data object ReloadLibrary : LibraryAction
data class LibraryShowClicked(val id: Long) : LibraryAction
