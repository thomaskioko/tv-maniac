package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.core.db.ShowsByCategory
import com.thomaskioko.tvmaniac.core.db.TrendingShows
import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import com.thomaskioko.tvmaniac.presentation.discover.model.TvShow
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun List<ShowsByCategory>?.toTvShowList(): ImmutableList<TvShow> =
    this?.map { it.toTvShow() }?.toImmutableList() ?: persistentListOf()

fun ShowsByCategory.toTvShow(): TvShow = TvShow(
    traktId = id.id,
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

fun List<TrendingShows>?.toDiscoverShowList(): ImmutableList<DiscoverShow> =
    this?.map { it.toDiscoverShow() }?.toImmutableList() ?: persistentListOf()

fun TrendingShows.toDiscoverShow(): DiscoverShow = DiscoverShow(
    tmdbId = id.id,
    posterImageUrl = poster_path,
    backdropImageUrl = backdrop_path,
)

fun getErrorMessage(
    trending: Either<Failure, List<ShowsByCategory>>,
    popular: Either<Failure, List<ShowsByCategory>>,
    anticipated: Either<Failure, List<ShowsByCategory>>,
    featuredShows: Either<Failure, List<TrendingShows>>,
) = trending.getErrorOrNull()?.errorMessage ?: popular.getErrorOrNull()?.errorMessage
    ?: anticipated.getErrorOrNull()?.errorMessage ?: featuredShows.getErrorOrNull()?.errorMessage
