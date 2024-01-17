package com.thomaskioko.tvmaniac.data.cast.testing

import com.thomaskioko.tvmaniac.core.db.SeasonCast
import com.thomaskioko.tvmaniac.core.db.ShowCast
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import kotlinx.coroutines.flow.Flow

class FakeCastRepository : CastRepository {
    override fun fetchSeasonCast(seasonId: Long): List<SeasonCast> {
        TODO("Not yet implemented")
    }

    override fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>> {
        TODO("Not yet implemented")
    }

    override fun fetchShowCast(showId: Long): List<ShowCast> {
        TODO("Not yet implemented")
    }

    override fun observeShowCast(showId: Long): Flow<List<ShowCast>> {
        TODO("Not yet implemented")
    }
}