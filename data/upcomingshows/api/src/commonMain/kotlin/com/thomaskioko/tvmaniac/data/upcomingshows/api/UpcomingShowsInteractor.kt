package com.thomaskioko.tvmaniac.data.upcomingshows.api

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class UpcomingShowsInteractor(
    private val upcomingShowsRepository: UpcomingShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Boolean>() {
    override suspend fun doWork(params: Boolean) {
        withContext(dispatchers.io) {
            upcomingShowsRepository.fetchUpcomingShows(params)
        }
    }
}
