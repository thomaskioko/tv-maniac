package com.thomaskioko.tvmaniac.episodes.api

import com.thomaskioko.tvmaniac.episodes.api.mapper.toEpisodeEntityList
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeUiModel
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class EpisodesInteractor constructor(
    private val repository: EpisodeRepository,
) : FlowInteractor<EpisodeQuery, List<EpisodeUiModel>>() {

    override fun run(params: EpisodeQuery): Flow<List<EpisodeUiModel>> =
        repository.observeSeasonEpisodes(
            tvShowId = params.tvShowId,
            seasonId = params.seasonId,
            seasonNumber = params.seasonNumber
        )
            .map { it.data?.toEpisodeEntityList() ?: emptyList() }
            .distinctUntilChanged()
}

data class EpisodeQuery(
    val tvShowId: Int,
    val seasonId: Int,
    val seasonNumber: Int
)
