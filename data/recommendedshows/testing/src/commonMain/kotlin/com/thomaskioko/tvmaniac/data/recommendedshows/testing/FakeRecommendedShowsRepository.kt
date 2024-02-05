package com.thomaskioko.tvmaniac.data.recommendedshows.testing

import com.thomaskioko.tvmaniac.core.db.RecommendedShows
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeRecommendedShowsRepository : RecommendedShowsRepository {

  private var showEntityList: Channel<List<RecommendedShows>> = Channel(Channel.UNLIMITED)
  private var entityListResult: Channel<Either<Failure, List<RecommendedShows>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setRecommendedShows(result: List<RecommendedShows>) {
    showEntityList.send(result)
  }

  suspend fun setObserveRecommendedShows(result: Either<Failure, List<RecommendedShows>>) {
    entityListResult.send(result)
  }

  override suspend fun fetchRecommendedShows(id: Long): List<RecommendedShows> {
    return showEntityList.receive()
  }

  override fun observeRecommendedShows(id: Long): Flow<Either<Failure, List<RecommendedShows>>> {
    return entityListResult.receiveAsFlow()
  }
}
