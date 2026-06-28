package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.trailers.implementation.TrailerRepository
import com.thomaskioko.tvmaniac.domain.showdetails.model.TrailersResult
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn

@Inject
public class ObserveTrailersInteractor(
    private val trailerRepository: TrailerRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Long, TrailersResult>() {

    override fun createObservable(params: Long): Flow<TrailersResult> =
        combine(
            trailerRepository.observeTrailers(params),
            trailerRepository.isYoutubePlayerInstalled(),
        ) { trailers, hasWebViewInstalled ->
            TrailersResult(
                trailers = trailers.toTrailerList(),
                hasWebViewInstalled = hasWebViewInstalled,
            )
        }.flowOn(dispatchers.io)
}
