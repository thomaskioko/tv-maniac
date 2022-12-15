package com.thomaskioko.tvmaniac.episodes.testing

import com.thomaskioko.tvmaniac.core.db.EpisodesByShowId
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeEpisodeRepository : EpisodeRepository {

    private var seasonEpisodes: Flow<List<EpisodesByShowId>> =
        flowOf(emptyList())

    fun setEpisode(result: List<EpisodesByShowId>) {
        seasonEpisodes = flow { emit(result) }
    }

    override fun observeSeasonEpisodes(seasonId: Int): Flow<List<EpisodesByShowId>> = seasonEpisodes

    override fun updateEpisodeArtWork(showId: Int): Flow<Unit> = flowOf(Unit)
}