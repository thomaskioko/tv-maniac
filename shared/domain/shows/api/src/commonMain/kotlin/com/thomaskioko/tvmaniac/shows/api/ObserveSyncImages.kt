package com.thomaskioko.tvmaniac.shows.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.shows.api.repository.TmdbRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn


class ObserveSyncImages(
    private val tmdbRepository: TmdbRepository,
    private val ioDispatcher: CoroutineDispatcher
) : FlowInteractor<Unit, Unit>() {

    override fun run(params: Unit): Flow<Unit> = tmdbRepository.observeUpdateShowArtWork()
        .flowOn(ioDispatcher)

}
