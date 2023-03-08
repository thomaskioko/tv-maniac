package com.thomaskioko.tvmaniac.shared.domain.discover

import com.thomaskioko.tvmaniac.core.db.SelectShowsByCategory
import com.thomaskioko.tvmaniac.core.util.network.Either
import com.thomaskioko.tvmaniac.core.util.network.Failure
import com.thomaskioko.tvmaniac.shared.domain.discover.model.TvShow
import com.thomaskioko.tvmaniac.category.api.model.Category


fun List<SelectShowsByCategory>.toTvShowList(): List<TvShow> = map { it.toTvShow() }

fun SelectShowsByCategory.toTvShow(): TvShow = TvShow(
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


fun Either<Failure, List<SelectShowsByCategory>>.toShowData(resultLimit: Int? = null): ShowResult.CategoryState =
    this.fold(
        {
            ShowResult.CategoryError(it.errorMessage)
        },
        { shows ->
            when {
                shows.isNullOrEmpty() -> ShowResult.EmptyCategoryData
                else -> {
                    ShowResult.CategorySuccess(
                        category = Category[shows.first().category_id!!],
                        tvShows = if (resultLimit != null) shows.toTvShowList().take(resultLimit)
                        else shows.toTvShowList()
                    )
                }
            }
        }
    )