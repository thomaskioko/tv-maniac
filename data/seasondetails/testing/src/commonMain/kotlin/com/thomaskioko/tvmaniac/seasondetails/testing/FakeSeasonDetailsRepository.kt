package com.thomaskioko.tvmaniac.seasondetails.testing

import com.thomaskioko.tvmaniac.core.db.Season_images
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasondetails.api.model.SeasonDetailsWithEpisodes
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSeasonDetailsRepository : SeasonDetailsRepository {

  private val seasonsResult: Channel<Either<Failure, SeasonDetailsWithEpisodes>> =
    Channel(Channel.UNLIMITED)

  suspend fun setSeasonsResult(result: Either<Failure, SeasonDetailsWithEpisodes>) {
    seasonsResult.send(result)
  }

  override fun observeSeasonDetails(
    param: SeasonDetailsParam,
  ): Flow<Either<Failure, SeasonDetailsWithEpisodes>> = seasonsResult.receiveAsFlow()

  override fun observeSeasonImages(id: Long): Flow<List<Season_images>> = flowOf(emptyList())
}
