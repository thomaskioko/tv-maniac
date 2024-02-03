package com.thomaskioko.tvmaniac.data.cast.testing

import com.thomaskioko.tvmaniac.core.db.SeasonCast
import com.thomaskioko.tvmaniac.core.db.ShowCast
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class FakeCastRepository : CastRepository {

    private var seasonCastEntityList: Channel<List<SeasonCast>> = Channel(Channel.UNLIMITED)
    private var showCastEntityList: Channel<List<ShowCast>> = Channel(Channel.UNLIMITED)

    suspend fun setSeasonCast(result: List<SeasonCast>) {
        seasonCastEntityList.send(result)
    }

    suspend fun setShowCast(result: List<ShowCast>) {
        showCastEntityList.send(result)
    }

    override suspend fun fetchShowCast(showId: Long): List<ShowCast> =
        showCastEntityList.receive()

    override suspend fun fetchSeasonCast(seasonId: Long): List<SeasonCast> =
        seasonCastEntityList.receive()

    override fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>> =
        seasonCastEntityList.receiveAsFlow()

    override fun observeShowCast(showId: Long): Flow<List<ShowCast>> =
        showCastEntityList.receiveAsFlow()
}
