package com.thomaskioko.tvmaniac.domain.rewatch

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class SyncRewatchInteractor(
    private val rewatchRepository: RewatchRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SyncRewatchInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            rewatchRepository.syncPendingRewatches()
            rewatchRepository.syncRewatchSessions(params.showId)
        }
    }

    public data class Param(
        val showId: Long,
    )
}
