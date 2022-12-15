package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.core.db.EpisodesByShowId
import kotlinx.coroutines.flow.Flow

interface EpisodeRepository {

    fun observeSeasonEpisodes(seasonId: Int): Flow<List<EpisodesByShowId>>

    fun updateEpisodeArtWork(showId: Int): Flow<Unit>
}
