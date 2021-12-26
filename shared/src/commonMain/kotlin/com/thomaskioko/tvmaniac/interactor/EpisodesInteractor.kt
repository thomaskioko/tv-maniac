package com.thomaskioko.tvmaniac.interactor

import com.thomaskioko.tvmaniac.core.usecase.FlowInteractor
import com.thomaskioko.tvmaniac.datasource.repository.episode.EpisodeRepository
import com.thomaskioko.tvmaniac.presentation.model.Episode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class EpisodesInteractor constructor(
    private val repository: EpisodeRepository,
) : FlowInteractor<EpisodeQuery, List<Episode>>() {

    override fun run(params: EpisodeQuery): Flow<List<Episode>> = flow {

        val result = repository.getEpisodesBySeasonId(
            tvShowId = params.tvShowId,
            seasonId = params.seasonId,
            seasonNumber = params.seasonNumber
        ).sortedBy { it.episodeNumber }

        emit(result)
    }.distinctUntilChanged()
}

data class EpisodeQuery(
    val tvShowId: Int,
    val seasonId: Int,
    val seasonNumber: Int
)
