package com.thomaskioko.tvmaniac.lastairepisodes.implementation

import com.thomaskioko.tvmaniac.core.db.AirEpisodesByShowId
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastAirEpisodeRepository
import com.thomaskioko.tvmaniac.lastairepisodes.api.LastEpisodeAirCache
import kotlinx.coroutines.flow.Flow

class LastAirEpisodeRepositoryImpl(
    private val epAirCacheLast: LastEpisodeAirCache
) : LastAirEpisodeRepository {

    override fun observeAirEpisodes(tvShowId: Int): Flow<List<AirEpisodesByShowId>> =
        epAirCacheLast.getShowAirEpisodes(showId = tvShowId)
}
