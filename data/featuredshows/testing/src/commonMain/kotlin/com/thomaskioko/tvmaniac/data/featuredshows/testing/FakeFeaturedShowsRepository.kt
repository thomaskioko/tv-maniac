package com.thomaskioko.tvmaniac.data.featuredshows.testing

import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeFeaturedShowsRepository : FeaturedShowsRepository {

  private var entityListResult: Channel<Either<Failure, List<ShowEntity>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setFeaturedShows(result: Either<Failure, List<ShowEntity>>) {
    entityListResult.send(result)
  }

  override suspend fun observeFeaturedShows(
    timeWindow: String,
    forceRefresh: Boolean
  ): Flow<Either<Failure, List<ShowEntity>>> = entityListResult.receiveAsFlow()
}
