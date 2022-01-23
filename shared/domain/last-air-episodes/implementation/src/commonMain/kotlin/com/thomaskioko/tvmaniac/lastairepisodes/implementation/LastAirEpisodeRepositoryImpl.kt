package com.thomaskioko.tvmaniac.lastairepisodes.implementation

import com.thomaskioko.tvmaniac.datasource.cache.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import kotlinx.coroutines.flow.Flow

class LastAirEpisodeRepositoryImpl(
    private val epAirCacheLast: LastEpisodeAirCache
) : LastAirEpisodeRepository {

    override fun observeAirEpisodes(tvShowId: Long): Flow<List<AirEpisodesByShowId>> =
        epAirCacheLast.getShowAirEpisodes(showId = tvShowId)
}
