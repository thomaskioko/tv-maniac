package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow
import kotlinx.collections.immutable.ImmutableList

sealed interface DiscoverState {
    val isContentEmpty: Boolean
}

object Loading : DiscoverState {
    override val isContentEmpty: Boolean = true
}

data class DataLoaded(
    val recommendedShows: ImmutableList<TvShow>? = null,
    val trendingShows: ImmutableList<TvShow>? = null,
    val popularShows: ImmutableList<TvShow>? = null,
    val anticipatedShows: ImmutableList<TvShow>? = null,
    val errorMessage: String? = null,
) : DiscoverState {
    override val isContentEmpty: Boolean = recommendedShows.isNullOrEmpty() &&
            trendingShows.isNullOrEmpty() && popularShows.isNullOrEmpty() && anticipatedShows.isNullOrEmpty()
}
