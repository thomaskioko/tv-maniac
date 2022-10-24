package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.core.db.SelectSeasonWithEpisodes
import com.thomaskioko.tvmaniac.core.util.network.Resource
import kotlinx.coroutines.flow.Flow

interface SeasonEpisodesRepository {
    fun observeSeasonEpisodes(
        showId: Int,
    ): Flow<Resource<List<SelectSeasonWithEpisodes>>>

   fun syncSeasonEpisodeArtWork(traktId: Int) : Flow<Unit>
}
