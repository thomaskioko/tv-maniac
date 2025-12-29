package com.thomaskioko.tvmaniac.data.cast.api

import com.thomaskioko.tvmaniac.db.SeasonCast
import com.thomaskioko.tvmaniac.db.ShowCast
import kotlinx.coroutines.flow.Flow

public interface CastRepository {
    public fun observeSeasonCast(seasonId: Long): Flow<List<SeasonCast>>

    public fun observeShowCast(showId: Long): Flow<List<ShowCast>>
}
