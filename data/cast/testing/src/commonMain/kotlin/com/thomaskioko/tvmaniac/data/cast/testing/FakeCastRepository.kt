package com.thomaskioko.tvmaniac.data.cast.testing

import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeCastRepository : CastRepository {

  private val seasonCastEntityList: Channel<List<SeasonCast>> = Channel(Channel.UNLIMITED)
  private val showCastEntityList: Channel<List<ShowCast>> = Channel(Channel.UNLIMITED)

  suspend fun setSeasonCast(result: List<SeasonCast>) {
    seasonCastEntityList.send(result)
  }

  suspend fun setShowCast(result: List<ShowCast>) {
    showCastEntityList.send(result)
  }

  override fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>> =
    seasonCastEntityList.receiveAsFlow()

  override fun observeShowCast(showId: Long): Flow<List<ShowCast>> =
    showCastEntityList.receiveAsFlow()
}
