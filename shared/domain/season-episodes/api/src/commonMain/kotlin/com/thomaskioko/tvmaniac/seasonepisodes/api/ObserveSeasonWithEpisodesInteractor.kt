package com.thomaskioko.tvmaniac.seasonepisodes.api

import com.thomaskioko.tvmaniac.seasonepisodes.api.model.SeasonWithEpisodes
import com.thomaskioko.tvmaniac.shared.core.FlowInteractor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ObserveSeasonWithEpisodesInteractor(
    private val repository: SeasonWithEpisodesRepository,
    private val dispatcher: CoroutineDispatcher,
) : FlowInteractor<Long, List<SeasonWithEpisodes>>() {

    override fun run(params: Long): Flow<List<SeasonWithEpisodes>> =
        repository.observeSeasonWithEpisodes(showId = params)
            .map { it.data?.toSeasonWithEpisodes() ?: emptyList() }
            .flowOn(dispatcher)
}
