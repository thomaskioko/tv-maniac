package com.thomaskioko.tvmaniac.presentation.search

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface SearchShowState

data object EmptyState: SearchShowState

data class ErrorState(val errorMessage: String?): SearchShowState

data class SearchResultAvailable(
  val isUpdating: Boolean = false,
  val result: ImmutableList<SearchResult> = persistentListOf(),
): SearchShowState

data class ShowContentAvailable(
  val isUpdating: Boolean = false,
  val errorMessage: String? = null,
  val featuredShows: ImmutableList<SearchResult> = persistentListOf(),
  val trendingShows: ImmutableList<SearchResult> = persistentListOf(),
  val upcomingShows: ImmutableList<SearchResult> = persistentListOf(),
): SearchShowState
