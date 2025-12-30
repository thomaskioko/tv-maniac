package com.thomaskioko.tvmaniac.data.popularshows.api

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class PopularShowsInteractor(
    private val popularShowsRepository: PopularShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Boolean>() {

    override suspend fun doWork(params: Boolean) {
        withContext(dispatchers.io) {
            popularShowsRepository.fetchPopularShows(params)
        }
    }
}
