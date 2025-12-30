package com.thomaskioko.tvmaniac.domain.similarshows

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.similarshows.SimilarShowsInteractor.Param
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class SimilarShowsInteractor(
    private val similarShowsRepository: SimilarShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {
    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            similarShowsRepository.fetchSimilarShows(id = params.id, forceRefresh = params.forceRefresh)
        }
    }

    public data class Param(val id: Long, val forceRefresh: Boolean = false)
}
