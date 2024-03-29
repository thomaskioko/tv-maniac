package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface DiscoverState

data object Loading : DiscoverState

data object EmptyState : DiscoverState

data class ErrorState(val errorMessage: String?) : DiscoverState

data class DataLoaded(
  val featuredShows: ImmutableList<DiscoverShow> = persistentListOf(),
  val topRatedShows: ImmutableList<DiscoverShow> = persistentListOf(),
  val popularShows: ImmutableList<DiscoverShow> = persistentListOf(),
  val upcomingShows: ImmutableList<DiscoverShow> = persistentListOf(),
  val trendingToday: ImmutableList<DiscoverShow> = persistentListOf(),
  val errorMessage: String? = null,
  val isRefreshing: Boolean = false,
) : DiscoverState
