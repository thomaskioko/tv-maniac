package com.thomaskioko.tvmaniac.presentation.search

import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun List<ShowEntity>?.toShowList(): ImmutableList<SearchResult> =
  this?.map {
    SearchResult(
      tmdbId = it.id,
      title = it.title,
      posterImageUrl = it.posterPath,
      inLibrary = it.inLibrary,
      status = it.status
    )
  }
    ?.toImmutableList()
    ?: persistentListOf()

fun ShowEntity.toSearchResult(): SearchResult = SearchResult(
  tmdbId = id,
  title = title,
  posterImageUrl = posterPath,
  inLibrary = inLibrary,
  overview = overview,
  status = status,
)

fun getErrorMessage(
  featuredShows: Either<Failure, List<ShowEntity>>,
  trendingShows: Either<Failure, List<ShowEntity>>,
  upcomingShows: Either<Failure, List<ShowEntity>>,
) =
  featuredShows.getErrorOrNull()?.errorMessage
    ?: trendingShows.getErrorOrNull()?.errorMessage
    ?: upcomingShows.getErrorOrNull()?.errorMessage
    ?: featuredShows.getErrorOrNull()?.errorMessage
