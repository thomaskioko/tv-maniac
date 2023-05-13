package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.category.api.model.Category
import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow

sealed interface ShowsState

object Loading : ShowsState

data class LoadingError(val errorMessage: String) : ShowsState

data class ShowsLoaded(
    val result: ShowResult,
) : ShowsState

data class ShowResult(
    val featuredCategoryState: CategoryState,
    val trendingCategoryState: CategoryState,
    val popularCategoryState: CategoryState,
    val anticipatedCategoryState: CategoryState,
) {

    sealed interface CategoryState

    data class CategoryError(
        val errorMessage: String,
    ) : CategoryState

    data class CategorySuccess(
        val category: Category,
        val tvShows: List<TvShow>,
    ) : CategoryState

    object EmptyCategoryData : CategoryState
}
