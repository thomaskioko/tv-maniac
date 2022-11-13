package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow


sealed interface ShowsState

object FetchShows  : ShowsState

object LoadShows : ShowsState

data class LoadingError(val errorMessage: String?) : ShowsState

data class ShowsLoaded(
    val result: ShowResult,
) : ShowsState

data class ShowResult(
    val featuredShows: ShowCategoryData,
    val trendingShows: ShowCategoryData,
    val popularShows: ShowCategoryData,
    val anticipatedShows: ShowCategoryData,
    val updateState: ShowUpdateState = ShowUpdateState.EMPTY
) {
    data class ShowCategoryData(
        val category: ShowCategory = ShowCategory.FEATURED,
        val tvShows: List<TvShow> = emptyList(),
    )
}


enum class ShowUpdateState {
    IDLE,
    /**
     *
     */
    EMPTY,

    /**
     * An error has occurred while updating the next state
     */
    ERROR,
}

sealed interface ShowsAction

object RetryLoading : ShowsAction
