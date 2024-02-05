package com.thomaskioko.tvmaniac.data.featuredshows.testing

import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.ShowEntity
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeFeaturedShowsRepository : FeaturedShowsRepository {

  private var showEntityList: Channel<List<ShowEntity>> = Channel(Channel.UNLIMITED)
  private var entityListResult: Channel<Either<Failure, List<ShowEntity>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setFeaturedShows(result: List<ShowEntity>) {
    showEntityList.send(result)
  }

  suspend fun setObserveFeaturedShows(result: Either<Failure, List<ShowEntity>>) {
    entityListResult.send(result)
  }

  override suspend fun fetchFeaturedShows(
    timeWindow: String,
    forceRefresh: Boolean,
  ): List<ShowEntity> = showEntityList.receive()

  override fun observeFeaturedShows(timeWindow: String): Flow<Either<Failure, List<ShowEntity>>> =
    entityListResult.receiveAsFlow()
}
