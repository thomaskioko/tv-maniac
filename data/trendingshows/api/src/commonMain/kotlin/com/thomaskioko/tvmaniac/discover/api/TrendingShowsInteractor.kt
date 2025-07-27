package com.thomaskioko.tvmaniac.discover.api

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class TrendingShowsInteractor(
    private val trendingShowsRepository: TrendingShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Boolean>() {
    override suspend fun doWork(params: Boolean) {
        withContext(dispatchers.io) {
            trendingShowsRepository.fetchTrendingShows(params)
        }
    }
}
