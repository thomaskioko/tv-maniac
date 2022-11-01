package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow

sealed interface ShowsState

object Loading : ShowsState

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

    sealed interface CategoryState

    data class CategoryError(
        val category: ShowCategory,
        val errorMessage: String?
    ) : CategoryState

    data class CategorySuccess(
        val category: ShowCategory,
        val tvShows: List<TvShow>,
    ) : CategoryState


    data class ShowCategoryData(
        val categoryState: CategoryState
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