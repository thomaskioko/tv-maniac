package com.thomaskioko.tvmaniac.domain.watchproviders

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderRepository
import com.thomaskioko.tvmaniac.domain.watchproviders.WatchProvidersInteractor.Param
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class WatchProvidersInteractor(
    private val repository: WatchProviderRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {
    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            repository.fetchWatchProviders(id = params.id, forceRefresh = params.forceRefresh)
        }
    }

    data class Param(val id: Long, val forceRefresh: Boolean = false)
}
