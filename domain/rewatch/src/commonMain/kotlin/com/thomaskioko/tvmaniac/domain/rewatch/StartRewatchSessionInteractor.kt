package com.thomaskioko.tvmaniac.domain.rewatch

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject

@Inject
public class StartRewatchSessionInteractor(
    private val rewatchRepository: RewatchRepository,
    private val dateTimeProvider: DateTimeProvider,
) : Interactor<StartRewatchSessionInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        if (rewatchRepository.openSessionForShow(params.showId) != null) return
        rewatchRepository.startSession(showId = params.showId, startedAt = dateTimeProvider.nowMillis())
    }

    public data class Param(
        val showId: Long,
    )
}
