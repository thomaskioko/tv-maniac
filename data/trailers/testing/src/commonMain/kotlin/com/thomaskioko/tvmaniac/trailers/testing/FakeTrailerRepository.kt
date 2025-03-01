package com.thomaskioko.tvmaniac.trailers.testing

import com.thomaskioko.tvmaniac.db.Trailers
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeTrailerRepository : TrailerRepository {
  private val youtubePlayerInstalled = Channel<Boolean>(Channel.UNLIMITED)
  private var response: Channel<Either<Failure, List<Trailers>>> = Channel(Channel.UNLIMITED)

  suspend fun setTrailerResult(result: Either<Failure, List<Trailers>>) {
    response.send(result)
  }

  suspend fun setYoutubePlayerInstalled(installed: Boolean) {
    youtubePlayerInstalled.send(installed)
  }

  override fun observeTrailers(id: Long): Flow<Either<Failure, List<Trailers>>> =
    response.receiveAsFlow()

  override fun isYoutubePlayerInstalled(): Flow<Boolean> = youtubePlayerInstalled.receiveAsFlow()
}
