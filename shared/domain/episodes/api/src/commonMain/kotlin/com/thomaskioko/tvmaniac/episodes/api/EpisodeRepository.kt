package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.db.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {

    fun observeSeasonEpisodes(
        tvShowId: Int,
        seasonId: Int,
        seasonNumber: Int
    ): Flow<Resource<List<EpisodesBySeasonId>>>
}
