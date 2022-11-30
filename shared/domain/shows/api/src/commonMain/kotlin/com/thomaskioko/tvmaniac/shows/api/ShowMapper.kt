package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.util.FormatterUtil
import com.thomaskioko.tvmaniac.core.util.network.Resource
import com.thomaskioko.tvmaniac.shows.api.model.ShowCategory
import com.thomaskioko.tvmaniac.shows.api.model.TvShow

fun List<SelectShowsByCategory>.toTvShowList(): List<TvShow> {
    return map { it.toTvShow() }
}

fun SelectShowsByCategory.toTvShow(): TvShow {
    return TvShow(
        traktId = trakt_id,
        tmdbId = tmdb_id,
        title = title,
        overview = overview,
        language = language,
        posterImageUrl = poster_url?.toImageUrl(),
        backdropImageUrl = backdrop_url?.toImageUrl(),
        votes = votes,
        rating = rating,
        genres = genres,
        year = year,
        status = status,
    )
}

fun String.toImageUrl() = FormatterUtil.formatPosterPath(this)

fun Resource<List<SelectShowsByCategory>>.toShowData(category: ShowCategory) =
    when (this) {
        is Resource.Error -> ShowResult.ShowCategoryData(
            ShowResult.CategoryError(category, errorMessage)
        )
        is Resource.Success -> ShowResult.ShowCategoryData(
            categoryState = ShowResult.CategorySuccess(
                category = category,
                tvShows = data?.toTvShowList() ?: emptyList()
            ),
        )
    }

fun Resource<List<SelectShowsByCategory>>.toShowData(
    category: ShowCategory,
    resultLimit: Int
) = when (this) {
    is Resource.Error -> ShowResult.ShowCategoryData(
        ShowResult.CategoryError(category, errorMessage)
    )
    is Resource.Success -> ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategorySuccess(
            category = category,
            tvShows = data?.toTvShowList()?.take(resultLimit) ?: emptyList()
        ),
    )
}