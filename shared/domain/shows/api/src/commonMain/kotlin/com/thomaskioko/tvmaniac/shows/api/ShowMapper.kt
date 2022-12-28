package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
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
        posterImageUrl = poster_url,
        backdropImageUrl = backdrop_url,
        votes = votes,
        rating = rating,
        genres = genres,
        year = year,
        status = status,
    )
}

fun Either<Failure, List<SelectShowsByCategory>>.toShowData(category: ShowCategory) =
    when (this) {
        is Either.Left -> ShowResult.ShowCategoryData(
            ShowResult.CategoryError(category, error.errorMessage)
        )

        is Either.Right -> ShowResult.ShowCategoryData(
            categoryState = ShowResult.CategorySuccess(
                category = category,
                tvShows = data?.toTvShowList() ?: emptyList()
            ),
        )
    }

fun Either<Failure, List<SelectShowsByCategory>>.toShowData(
    category: ShowCategory,
    resultLimit: Int
) = when (this) {
    is Either.Left -> ShowResult.ShowCategoryData(
        ShowResult.CategoryError(category, error.errorMessage)
    )

    is Either.Right -> ShowResult.ShowCategoryData(
        categoryState = ShowResult.CategorySuccess(
            category = category,
            tvShows = data?.toTvShowList()?.take(resultLimit) ?: emptyList()
        ),
    )
}