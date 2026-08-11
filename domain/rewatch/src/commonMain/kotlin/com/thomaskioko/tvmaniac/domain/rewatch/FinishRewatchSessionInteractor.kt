package com.thomaskioko.tvmaniac.domain.rewatch

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import dev.zacsweers.metro.Inject

@Inject
public class FinishRewatchSessionInteractor(
    private val rewatchRepository: RewatchRepository,
) : Interactor<FinishRewatchSessionInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        rewatchRepository.finishSession(sessionId = params.sessionId, closedAt = params.closedAt)
    }

    public data class Param(
        val sessionId: Long,
        val closedAt: Long,
    )
}
