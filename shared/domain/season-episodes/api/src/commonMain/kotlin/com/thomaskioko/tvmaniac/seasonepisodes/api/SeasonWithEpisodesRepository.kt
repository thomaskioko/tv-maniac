package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.datasource.cache.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.shared.core.util.Resource
import kotlinx.coroutines.flow.Flow

interface SeasonWithEpisodesRepository {
    fun observeSeasonWithEpisodes(
        showId: Long
    ): Flow<Resource<List<SelectSeasonWithEpisodes>>>
}
