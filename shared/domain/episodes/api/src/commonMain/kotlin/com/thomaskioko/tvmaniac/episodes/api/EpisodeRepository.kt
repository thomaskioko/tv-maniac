package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.datasource.cache.EpisodesBySeasonId
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {

    fun observeSeasonEpisodes(
        tvShowId: Long,
        seasonId: Long,
        seasonNumber: Int
    ): Flow<Resource<List<EpisodesBySeasonId>>>
}
