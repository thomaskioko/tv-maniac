package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.showcommon.api.repository.TvShowsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

class ObserveSeasonWithEpisodesInteractor(
    private val tvShowsRepository: TvShowsRepository,
    private val repository: SeasonWithEpisodesRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowInteractor<Long, SeasonsResult>() {

    override fun run(params: Long): Flow<SeasonsResult> = combine(
        tvShowsRepository.observeShow(params),
        repository.observeSeasonWithEpisodes(showId = params)
    ) { show, seasons ->
        SeasonsResult(
            tvShow = show.toTvShow(),
            seasonsWithEpisodes = seasons.toSeasonWithEpisodes()
        )
    }
        .flowOn(dispatcher)
}
