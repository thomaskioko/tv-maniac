package com.thomaskioko.tvmaniac.data.featuredshows.api.interactor

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class FeaturedShowsInteractor(
    private val featuredShowsRepository: FeaturedShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Boolean>() {
    override suspend fun doWork(params: Boolean) {
        withContext(dispatchers.io) {
            featuredShowsRepository.fetchFeaturedShows(params)
        }
    }
}
