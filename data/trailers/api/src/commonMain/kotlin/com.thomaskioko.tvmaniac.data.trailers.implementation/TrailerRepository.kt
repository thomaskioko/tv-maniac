package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.SelectByShowId
import kotlinx.coroutines.flow.Flow

public interface TrailerRepository {
    public fun isYoutubePlayerInstalled(): Flow<Boolean>

    public fun observeTrailers(showId: Long): Flow<List<SelectByShowId>>

    public suspend fun fetchTrailers(showId: Long, forceRefresh: Boolean = false)
}
