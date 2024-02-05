package com.thomaskioko.tvmaniac.presentation.watchlist

import com.thomaskioko.tvmaniac.presentation.watchlist.model.LibraryItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface LibraryState

data object LoadingShows : LibraryState

data class LibraryContent(
  val list: ImmutableList<LibraryItem> = persistentListOf(),
) : LibraryState

data class ErrorLoadingShows(val message: String? = null) : LibraryState
