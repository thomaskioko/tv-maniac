package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.showcommon.api.repository.TmdbRepository
import kotlinx.coroutines.flow.Flow


class ObserveSyncImages constructor(
    private val tmdbRepository: TmdbRepository
) : FlowInteractor<Unit, Unit>() {

    override fun run(params: Unit): Flow<Unit> = tmdbRepository.observeUpdateShowArtWork()

}
