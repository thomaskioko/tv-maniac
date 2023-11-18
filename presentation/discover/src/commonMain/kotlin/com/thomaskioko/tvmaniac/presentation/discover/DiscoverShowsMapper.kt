package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.core.networkutil.Either
import com.thomaskioko.tvmaniac.core.networkutil.Failure
import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun List<ShowsByCategory>?.toTvShowList(): ImmutableList<TvShow> =
    this?.map { it.toTvShow() }?.toImmutableList() ?: persistentListOf()

fun ShowsByCategory.toTvShow(): TvShow = TvShow(
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

fun getErrorMessage(
    trending: Either<Failure, List<ShowsByCategory>>,
    popular: Either<Failure, List<ShowsByCategory>>,
    anticipated: Either<Failure, List<ShowsByCategory>>,
    recommended: Either<Failure, List<ShowsByCategory>>,
) = trending.getErrorOrNull()?.errorMessage ?: popular.getErrorOrNull()?.errorMessage
    ?: anticipated.getErrorOrNull()?.errorMessage ?: recommended.getErrorOrNull()?.errorMessage

fun getIsContentEmpty(
    trending: Either<Failure, List<ShowsByCategory>>,
    popular: Either<Failure, List<ShowsByCategory>>,
    anticipated: Either<Failure, List<ShowsByCategory>>,
    recommended: Either<Failure, List<ShowsByCategory>>,
) = trending.getOrNull().isNullOrEmpty() && popular.getOrNull().isNullOrEmpty() &&
    anticipated.getOrNull().isNullOrEmpty() && recommended.getOrNull().isNullOrEmpty()
