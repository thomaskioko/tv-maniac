package com.thomaskioko.tvmaniac.data.featuredshows.api

import com.thomaskioko.tvmaniac.shows.api.DEFAULT_DAY_TIME_WINDOW
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.flow.Flow

interface FeaturedShowsRepository {

  suspend fun fetchFeaturedShows(
    timeWindow: String = DEFAULT_DAY_TIME_WINDOW,
    forceRefresh: Boolean = false,
  ): List<ShowEntity>

  fun observeFeaturedShows(
    timeWindow: String = DEFAULT_DAY_TIME_WINDOW,
  ): Flow<Either<Failure, List<ShowEntity>>>
}
