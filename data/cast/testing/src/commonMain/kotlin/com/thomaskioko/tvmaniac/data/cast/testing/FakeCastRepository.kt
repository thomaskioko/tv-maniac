package com.thomaskioko.tvmaniac.data.cast.testing

import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

public class FakeCastRepository : CastRepository {

    private val seasonCastEntityList: Channel<List<SeasonCast>> = Channel(Channel.UNLIMITED)
    private val showCastEntityList: Channel<List<ShowCast>> = Channel(Channel.UNLIMITED)

    public suspend fun setSeasonCast(result: List<SeasonCast>) {
        seasonCastEntityList.send(result)
    }

    public suspend fun setShowCast(result: List<ShowCast>) {
        showCastEntityList.send(result)
    }

    override suspend fun fetchShowCast(showTraktId: Long, forceRefresh: Boolean) {
    }

    override fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>> =
        seasonCastEntityList.receiveAsFlow()

    override fun observeShowCast(showTraktId: Long): Flow<List<ShowCast>> =
        showCastEntityList.receiveAsFlow()
}
