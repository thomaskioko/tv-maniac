package com.thomaskioko.tvmaniac.presentation.watchlist

sealed interface LibraryAction

data object ReloadLibrary : LibraryAction
