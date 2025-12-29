package com.thomaskioko.tvmaniac.topratedshows.data.api

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class TopRatedShowsInteractor(
    private val topRatedShowsRepository: TopRatedShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Boolean>() {
    override suspend fun doWork(params: Boolean) {
        withContext(dispatchers.io) {
            topRatedShowsRepository.fetchTopRatedShows(params)
        }
    }
}
