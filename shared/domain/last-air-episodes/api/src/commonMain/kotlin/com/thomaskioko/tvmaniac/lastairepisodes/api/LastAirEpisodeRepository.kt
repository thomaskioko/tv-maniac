package com.thomaskioko.tvmaniac.lastairepisodes.api

import com.thomaskioko.tvmaniac.core.db.AirEpisodesByShowId
import kotlinx.coroutines.flow.Flow

interface LastAirEpisodeRepository {
    fun observeAirEpisodes(tvShowId: Int): Flow<List<AirEpisodesByShowId>>
}
