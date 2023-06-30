package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow

sealed interface DiscoverState {
    val isContentEmpty: Boolean
}

object Loading : DiscoverState {
    override val isContentEmpty: Boolean = true
}

data class DiscoverContent(
    val recommendedShows: List<TvShow> = emptyList(),
    val trendingShows: List<TvShow> = emptyList(),
    val popularShows: List<TvShow> = emptyList(),
    val anticipatedShows: List<TvShow> = emptyList(),
    val errorMessage: String? = null,
) : DiscoverState {

    override val isContentEmpty: Boolean = recommendedShows.isEmpty() &&
        trendingShows.isEmpty() && popularShows.isEmpty() && anticipatedShows.isEmpty()
}
