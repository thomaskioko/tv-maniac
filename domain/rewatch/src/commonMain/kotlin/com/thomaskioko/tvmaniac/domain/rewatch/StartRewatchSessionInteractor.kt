package com.thomaskioko.tvmaniac.domain.rewatch

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import dev.zacsweers.metro.Inject

@Inject
public class StartRewatchSessionInteractor(
    private val rewatchRepository: RewatchRepository,
) : Interactor<StartRewatchSessionInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        rewatchRepository.startSession(showId = params.showId, startedAt = params.startedAt)
    }

    public data class Param(
        val showId: Long,
        val startedAt: Long,
    )
}
