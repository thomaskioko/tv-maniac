package com.thomaskioko.tvmaniac.domain.rewatch

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject

@Inject
public class FinishRewatchSessionInteractor(
    private val rewatchRepository: RewatchRepository,
    private val dateTimeProvider: DateTimeProvider,
) : Interactor<FinishRewatchSessionInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        val sessionId = rewatchRepository.openSessionForShow(params.showId)?.id ?: return
        rewatchRepository.finishSession(sessionId = sessionId, closedAt = dateTimeProvider.nowMillis())
    }

    public data class Param(
        val showId: Long,
    )
}
