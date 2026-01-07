package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.SelectByShowId
import kotlinx.coroutines.flow.Flow

public interface TrailerRepository {
    public fun isYoutubePlayerInstalled(): Flow<Boolean>

    public fun observeTrailers(id: Long): Flow<List<SelectByShowId>>
}
