package com.thomaskioko.tvmaniac.similar.testing

import com.thomaskioko.tvmaniac.core.db.SimilarShows
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSimilarShowsRepository : SimilarShowsRepository {

  private val similarShows = Channel<Either<Failure, List<SimilarShows>>>(Channel.UNLIMITED)

  suspend fun setSimilarShowsResult(result: Either<Failure, List<SimilarShows>>) {
    similarShows.send(result)
  }

  override fun observeSimilarShows(
    id: Long,
    forceReload: Boolean
  ): Flow<Either<Failure, List<SimilarShows>>> = similarShows.receiveAsFlow()
}
