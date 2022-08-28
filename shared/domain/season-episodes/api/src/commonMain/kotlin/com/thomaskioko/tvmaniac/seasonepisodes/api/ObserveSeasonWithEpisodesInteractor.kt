package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.showcommon.api.repository.TmdbRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class ObserveSeasonWithEpisodesInteractor(
    private val tmdbRepository: TmdbRepository,
    private val repository: SeasonWithEpisodesRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowInteractor<Long, SeasonsResult>() {

    override fun run(params: Long): Flow<SeasonsResult> = combine(
        tmdbRepository.observeShow(params),
        repository.observeSeasonWithEpisodes(showId = params)
    ) { show, seasons ->
        SeasonsResult(
            tvShow = show.toTvShow(),
            seasonsWithEpisodes = seasons.toSeasonWithEpisodes()
        )
    }
        .flowOn(dispatcher)
}
