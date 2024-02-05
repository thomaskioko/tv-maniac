package com.thomaskioko.tvmaniac.seasons.testing

import com.thomaskioko.tvmaniac.core.db.ShowSeasons
import com.thomaskioko.tvmaniac.seasons.api.SeasonsRepository
import com.thomaskioko.tvmaniac.util.model.Either
import com.thomaskioko.tvmaniac.util.model.Failure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeSeasonsRepository : SeasonsRepository {

  private var seasonsList: Channel<List<ShowSeasons>> = Channel(Channel.UNLIMITED)
  private var seasonsResult: Channel<Either<Failure, List<ShowSeasons>>> =
    Channel(Channel.UNLIMITED)

  suspend fun setSeasons(result: List<ShowSeasons>) {
    seasonsList.send(result)
  }

  suspend fun setSeasonsResult(result: Either<Failure, List<ShowSeasons>>) {
    seasonsResult.send(result)
  }

  override suspend fun fetchSeasonsByShowId(id: Long): List<ShowSeasons> = seasonsList.receive()

  override fun observeSeasonsByShowId(id: Long): Flow<Either<Failure, List<ShowSeasons>>> =
    seasonsResult.receiveAsFlow()
}
