package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow

sealed interface DiscoverState

object Loading : DiscoverState

data class DataLoaded(
    val recommendedShows: List<TvShow>? = null,
    val trendingShows: List<TvShow>? = null,
    val popularShows: List<TvShow>? = null,
    val anticipatedShows: List<TvShow>? = null,
    val errorMessage: String? = null,
    val isContentEmpty: Boolean = true,
) : DiscoverState
