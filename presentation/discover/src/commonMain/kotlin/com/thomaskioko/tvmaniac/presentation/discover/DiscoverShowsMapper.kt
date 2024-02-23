package com.thomaskioko.tvmaniac.presentation.discover

import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.presentation.discover.model.DiscoverShow
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun List<ShowEntity>?.toShowList(): ImmutableList<DiscoverShow> =
  this?.map {
      DiscoverShow(
        tmdbId = it.id,
        title = it.title,
        posterImageUrl = it.posterPath,
        inLibrary = it.inLibrary,
      )
    }
    ?.toImmutableList()
    ?: persistentListOf()

fun getErrorMessage(
  topRated: Either<Failure, List<ShowEntity>>,
  popular: Either<Failure, List<ShowEntity>>,
  upcomingShows: Either<Failure, List<ShowEntity>>,
  featuredShows: Either<Failure, List<ShowEntity>>,
) =
  topRated.getErrorOrNull()?.errorMessage
    ?: popular.getErrorOrNull()?.errorMessage ?: upcomingShows.getErrorOrNull()?.errorMessage
      ?: featuredShows.getErrorOrNull()?.errorMessage
