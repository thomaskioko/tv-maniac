package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow
import kotlinx.collections.immutable.ImmutableList

sealed interface DiscoverState

object Loading : DiscoverState

data class DataLoaded(
    val recommendedShows: ImmutableList<TvShow>? = null,
    val trendingShows: ImmutableList<TvShow>? = null,
    val popularShows: ImmutableList<TvShow>? = null,
    val anticipatedShows: ImmutableList<TvShow>? = null,
    val errorMessage: String? = null,
    val isContentEmpty: Boolean = true,
) : DiscoverState
