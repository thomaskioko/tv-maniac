package com.thomaskioko.tvmaniac.data.featuredshows.api

import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.shows.api.DEFAULT_DAY_TIME_WINDOW
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.flow.Flow

interface FeaturedShowsRepository {
  suspend fun updateFeaturedShows(
    timeWindow: String = DEFAULT_DAY_TIME_WINDOW,
    forceRefresh: Boolean = false,
  ): Flow<Either<Failure, List<ShowEntity>>>
}
