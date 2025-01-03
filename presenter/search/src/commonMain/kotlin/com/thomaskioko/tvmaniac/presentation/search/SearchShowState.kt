package com.thomaskioko.tvmaniac.presentation.search

import com.thomaskioko.tvmaniac.presentation.search.model.ShowGenre
import com.thomaskioko.tvmaniac.presentation.search.model.ShowItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface SearchShowState {
  val query: String?
  val isUpdating: Boolean
}

data class InitialSearchState(
  override val query: String? = null,
  override val isUpdating: Boolean = true,
): SearchShowState

data class EmptySearchResult(
  override val query: String? = null,
  override val isUpdating: Boolean = false,
  val errorMessage: String? = null,
): SearchShowState

data class SearchResultAvailable(
  override val query: String? = null,
  override val isUpdating: Boolean = false,
  val errorMessage: String? = null,
  val results: ImmutableList<ShowItem>? = null,
): SearchShowState

data class ShowContentAvailable(
  override val query: String? = null,
  override val isUpdating: Boolean = false,
  val errorMessage: String? = null,
  val genres: ImmutableList<ShowGenre> = persistentListOf(),
): SearchShowState
