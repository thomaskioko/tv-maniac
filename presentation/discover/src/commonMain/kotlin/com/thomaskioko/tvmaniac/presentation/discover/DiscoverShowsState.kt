package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

sealed interface DiscoverState

data object Loading : DiscoverState
data class ErrorState(val errorMessage: String?) : DiscoverState

data class DataLoaded(
    val featuredShows: ImmutableList<DiscoverShow> = persistentListOf(),
    val trendingShows: ImmutableList<TvShow> = persistentListOf(),
    val popularShows: ImmutableList<TvShow> = persistentListOf(),
    val anticipatedShows: ImmutableList<TvShow> = persistentListOf(),
    val trendingToday: ImmutableList<DiscoverShow> = persistentListOf(),
    val errorMessage: String? = null,
) : DiscoverState
