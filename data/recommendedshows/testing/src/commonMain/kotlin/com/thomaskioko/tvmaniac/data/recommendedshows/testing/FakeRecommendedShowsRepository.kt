package com.thomaskioko.tvmaniac.data.recommendedshows.testing

import com.thomaskioko.tvmaniac.db.RecommendedShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeRecommendedShowsRepository : RecommendedShowsRepository {

  private var entityListResult: Channel<Either<Failure, List<RecommendedShows>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setObserveRecommendedShows(result: Either<Failure, List<RecommendedShows>>) {
    entityListResult.send(result)
  }

  override fun observeRecommendedShows(
    id: Long,
    forceReload: Boolean
  ): Flow<Either<Failure, List<RecommendedShows>>> {
    return entityListResult.receiveAsFlow()
  }
}
