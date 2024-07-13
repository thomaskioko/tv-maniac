package com.thomaskioko.tvmaniac.seasons.testing

import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.core.networkutil.model.Failure
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSeasonsRepository : SeasonsRepository {

  private var seasonsResult: Channel<Either<Failure, List<ShowSeasons>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setSeasonsResult(result: Either<Failure, List<ShowSeasons>>) {
    seasonsResult.send(result)
  }

  override fun observeSeasonsByShowId(id: Long): Flow<Either<Failure, List<ShowSeasons>>> =
    seasonsResult.receiveAsFlow()
}
