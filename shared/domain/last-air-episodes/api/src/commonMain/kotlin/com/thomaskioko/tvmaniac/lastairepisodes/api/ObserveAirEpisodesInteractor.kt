package com.thomaskioko.tvmaniac.lastairepisodes.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveAirEpisodesInteractor constructor(
    private val repository: LastAirEpisodeRepository,
) : FlowInteractor<Long, List<LastAirEpisode>>() {

    override fun run(params: Long): Flow<List<LastAirEpisode>> =
        repository.observeAirEpisodes(params)
            .map { it.toLastAirEpisodeList() }
            .distinctUntilChanged()
}
