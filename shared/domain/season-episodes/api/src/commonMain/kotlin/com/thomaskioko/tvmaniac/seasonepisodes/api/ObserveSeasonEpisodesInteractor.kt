package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class ObserveSeasonEpisodesInteractor(
    private val tmdbRepository: TmdbRepository,
    private val repository: SeasonWithEpisodesRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowInteractor<Int, SeasonsResult>() {

    override fun run(params: Int): Flow<SeasonsResult> = combine(
        tmdbRepository.observeShow(params),
        repository.observeSeasonEpisodes(showId = params)
    ) { show, seasons ->
        SeasonsResult(
            tvShow = show.toTvShow(),
            seasonsWithEpisodes = seasons.toSeasonWithEpisodes()
        )
    }
        .flowOn(dispatcher)
}
