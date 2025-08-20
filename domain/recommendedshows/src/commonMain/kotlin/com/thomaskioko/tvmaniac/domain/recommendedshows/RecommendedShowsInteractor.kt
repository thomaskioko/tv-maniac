package com.thomaskioko.tvmaniac.domain.recommendedshows

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.recommendedshows.api.RecommendedShowsRepository
import com.thomaskioko.tvmaniac.domain.recommendedshows.RecommendedShowsInteractor.Param
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class RecommendedShowsInteractor(
    private val recommendedShowsRepository: RecommendedShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {
    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            recommendedShowsRepository.fetchRecommendedShows(id = params.id, forceRefresh = params.forceRefresh)
        }
    }

    data class Param(val id: Long, val forceRefresh: Boolean = false)
}
