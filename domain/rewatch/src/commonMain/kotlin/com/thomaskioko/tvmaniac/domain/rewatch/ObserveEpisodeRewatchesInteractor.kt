package com.thomaskioko.tvmaniac.domain.rewatch

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveEpisodeRewatchesInteractor(
    private val rewatchRepository: RewatchRepository,
) : SubjectInteractor<Long, Long>() {

    override fun createObservable(params: Long): Flow<Long> =
        rewatchRepository.observeEpisodeRewatches(params)
}
