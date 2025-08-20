package com.thomaskioko.tvmaniac.topratedshows.data.api

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class TopRatedShowsInteractor(
    private val topRatedShowsRepository: TopRatedShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Boolean>() {
    override suspend fun doWork(params: Boolean) {
        withContext(dispatchers.io) {
            topRatedShowsRepository.fetchTopRatedShows(params)
        }
    }
}
