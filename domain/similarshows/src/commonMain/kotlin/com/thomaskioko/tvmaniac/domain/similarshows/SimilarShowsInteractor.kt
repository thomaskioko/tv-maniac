package com.thomaskioko.tvmaniac.domain.similarshows

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor.Param
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class SimilarShowsInteractor(
    private val similarShowsRepository: SimilarShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {
    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            similarShowsRepository.fetchSimilarShows(id = params.id, forceRefresh = params.forceRefresh)
        }
    }

    data class Param(val id: Long, val forceRefresh: Boolean = false)
}
