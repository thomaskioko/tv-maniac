package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow

sealed interface DiscoverState

object Loading : DiscoverState

data class ContentError(val errorMessage: String) : DiscoverState

data class DiscoverContent(
    val contentState: DiscoverContentState,
) : DiscoverState {

    sealed interface DiscoverContentState

    data class DataLoaded(
        val recommendedShows: List<TvShow> = emptyList(),
        val trendingShows: List<TvShow> = emptyList(),
        val popularShows: List<TvShow> = emptyList(),
        val anticipatedShows: List<TvShow> = emptyList(),
    ) : DiscoverContentState

    object EmptyResult : DiscoverContentState
}
