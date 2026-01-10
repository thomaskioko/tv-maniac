package com.thomaskioko.tvmaniac.data.trailers.implementation

import com.thomaskioko.tvmaniac.db.SelectByShowTmdbId
import kotlinx.coroutines.flow.Flow

public interface TrailerRepository {
    public fun isYoutubePlayerInstalled(): Flow<Boolean>

    public fun observeTrailers(id: Long): Flow<List<SelectByShowTmdbId>>
}
