package com.thomaskioko.tvmaniac.presentation.search

import kotlinx.collections.immutable.ImmutableList

sealed interface SearchShowState {
  val query: String?
}

data class EmptySearchState(override val query: String? = null): SearchShowState

data class ErrorSearchState(
  override val query: String? = null,
  val errorMessage: String?,
): SearchShowState

data class SearchResultAvailable(
  override val query: String? = null,
  val isUpdating: Boolean = false,
  val results: ImmutableList<ShowItem>? = null,
): SearchShowState

data class ShowContentAvailable(
  override val query: String? = null,
  val isUpdating: Boolean = false,
  val errorMessage: String? = null,
  val featuredShows: ImmutableList<ShowItem>? = null,
  val trendingShows: ImmutableList<ShowItem>? = null,
  val upcomingShows: ImmutableList<ShowItem>? = null,
): SearchShowState
