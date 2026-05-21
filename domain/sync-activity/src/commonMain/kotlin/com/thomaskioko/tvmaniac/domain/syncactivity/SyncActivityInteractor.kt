package com.thomaskioko.tvmaniac.domain.syncactivity

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class SyncActivityInteractor(
    private val traktActivityRepository: TraktActivityRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<SyncActivityInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            traktActivityRepository.fetchLatestActivities(params.forceRefresh)
        }
    }

    public data class Param(
        val forceRefresh: Boolean = false,
    )
}
