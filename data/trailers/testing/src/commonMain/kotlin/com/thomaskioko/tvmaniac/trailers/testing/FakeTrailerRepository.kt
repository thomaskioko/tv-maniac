package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.core.db.Trailers
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow

class FakeTrailerRepository : TrailerRepository {
  private var trailerList: Channel<List<Trailers>> = Channel(Channel.UNLIMITED)
  private var trailersStoreResponse: Channel<Either<Failure, List<Trailers>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setTrailerResult(result: Either<Failure, List<Trailers>>) {
    trailersStoreResponse.send(result)
  }

  suspend fun setTrailerList(list: List<Trailers>) {
    trailerList.send(list)
  }

  override fun isYoutubePlayerInstalled(): Flow<Boolean> = flowOf()

  override fun observeTrailersStoreResponse(id: Long): Flow<Either<Failure, List<Trailers>>> =
    trailersStoreResponse.receiveAsFlow()

  override suspend fun fetchTrailersByShowId(id: Long): List<Trailers> = trailerList.receive()
}
