package com.thomaskioko.tvmaniac.lastairepisodes.api

import com.thomaskioko.tvmaniac.datasource.cache.AirEpisodesByShowId
import kotlinx.coroutines.flow.Flow

interface LastAirEpisodeRepository {
    fun observeAirEpisodes(tvShowId: Long): Flow<List<AirEpisodesByShowId>>
}
