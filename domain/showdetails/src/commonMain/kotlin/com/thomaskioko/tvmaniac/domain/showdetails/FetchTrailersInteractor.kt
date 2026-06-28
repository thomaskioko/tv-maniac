package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.domain.showdetails.FetchTrailersInteractor.Param
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
public class FetchTrailersInteractor(
    private val trailerRepository: TrailerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {

    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            trailerRepository.fetchTrailers(showId = params.id, forceRefresh = params.forceRefresh)
        }
    }

    public data class Param(val id: Long, val forceRefresh: Boolean = false)
}
