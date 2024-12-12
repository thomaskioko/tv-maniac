package com.thomaskioko.tvmaniac.presentation.search

import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.presentation.search.model.ShowItem
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.util.FormatterUtil
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import me.tatarka.inject.annotations.Inject


@Inject
class ShowMapper(
  private val formatterUtil: FormatterUtil
) {

  fun toShowList(items: List<ShowEntity>?): ImmutableList<ShowItem>? =
    items?.map {
        ShowItem(
            tmdbId = it.id,
            title = it.title,
            posterImageUrl = it.posterPath,
            inLibrary = it.inLibrary,
            status = it.status,
            voteAverage = it.voteAverage?.let { vote -> formatterUtil.formatDouble(vote, 1) },
            year = it.year,
            overview = it.overview,
        )
    }
      ?.toImmutableList()

  fun getErrorMessage(
    featuredShows: Either<Failure, List<ShowEntity>>,
    trendingShows: Either<Failure, List<ShowEntity>>,
    upcomingShows: Either<Failure, List<ShowEntity>>,
  ) =
    featuredShows.getErrorOrNull()?.errorMessage
      ?: trendingShows.getErrorOrNull()?.errorMessage
      ?: upcomingShows.getErrorOrNull()?.errorMessage
      ?: featuredShows.getErrorOrNull()?.errorMessage
}

