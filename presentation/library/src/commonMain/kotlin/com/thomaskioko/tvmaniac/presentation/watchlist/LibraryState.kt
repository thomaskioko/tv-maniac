package com.thomaskioko.tvmaniac.presentation.watchlist

sealed interface LibraryState

data object LoadingShows : LibraryState

data class LibraryContent(
    val list: List<LibraryItem> = emptyList(),
) : LibraryState

data class ErrorLoadingShows(val message: String? = null) : LibraryState
