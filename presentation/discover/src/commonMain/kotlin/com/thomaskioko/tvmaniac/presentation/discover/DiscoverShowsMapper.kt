package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.core.db.PagedPopularShows
import com.thomaskioko.tvmaniac.core.db.PagedTopRatedShows
import com.thomaskioko.tvmaniac.core.db.TrendingShows
import com.thomaskioko.tvmaniac.core.db.UpcomingShows
import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun List<TrendingShows>?.toDiscoverShowList(): ImmutableList<DiscoverShow> =
    this?.map {
        DiscoverShow(
            tmdbId = it.id.id,
            posterImageUrl = it.poster_path,
            backdropImageUrl = it.backdrop_path,
            isInLibrary = it.in_library == 1L,
        )
    }?.toImmutableList() ?: persistentListOf()

fun List<UpcomingShows>?.toUpcomingShowList(): ImmutableList<DiscoverShow> =
    this?.map {
        DiscoverShow(
            tmdbId = it.id.id,
            posterImageUrl = it.poster_path,
            backdropImageUrl = it.backdrop_path,
            isInLibrary = it.in_library == 1L,
        )
    }?.toImmutableList() ?: persistentListOf()

fun List<PagedTopRatedShows>?.toTopRatedList(): ImmutableList<DiscoverShow> =
    this?.map {
        DiscoverShow(
            tmdbId = it.id.id,
            posterImageUrl = it.poster_path,
            backdropImageUrl = it.backdrop_path,
            isInLibrary = it.in_library == 1L,
        )
    }?.toImmutableList() ?: persistentListOf()

fun List<PagedPopularShows>?.toPopularList(): ImmutableList<DiscoverShow> =
    this?.map {
        DiscoverShow(
            tmdbId = it.id.id,
            posterImageUrl = it.poster_path,
            backdropImageUrl = it.backdrop_path,
            isInLibrary = it.in_library == 1L,
        )
    }?.toImmutableList() ?: persistentListOf()

fun getErrorMessage(
    topRated: Either<Failure, List<PagedTopRatedShows>>,
    popular: Either<Failure, List<PagedPopularShows>>,
    upcomingShows: Either<Failure, List<UpcomingShows>>,
    featuredShows: Either<Failure, List<TrendingShows>>,
) = topRated.getErrorOrNull()?.errorMessage ?: popular.getErrorOrNull()?.errorMessage
    ?: upcomingShows.getErrorOrNull()?.errorMessage ?: featuredShows.getErrorOrNull()?.errorMessage
