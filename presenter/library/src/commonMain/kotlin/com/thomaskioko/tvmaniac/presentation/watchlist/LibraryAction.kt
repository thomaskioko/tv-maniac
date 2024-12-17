package com.thomaskioko.tvmaniac.presentation.watchlist

sealed interface LibraryAction

data object ReloadLibrary : LibraryAction

data class LibraryShowClicked(val id: Long) : LibraryAction

data class LibraryQueryChanged(val query: String) : LibraryAction

data object ClearLibraryQuery : LibraryAction

data object ChangeListStyleClicked : LibraryAction
