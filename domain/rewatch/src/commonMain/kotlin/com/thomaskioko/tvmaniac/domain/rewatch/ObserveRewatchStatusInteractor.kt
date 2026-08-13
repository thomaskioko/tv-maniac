package com.thomaskioko.tvmaniac.domain.rewatch

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchStatus
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveRewatchStatusInteractor(
    private val rewatchRepository: RewatchRepository,
) : SubjectInteractor<Long, RewatchStatus>() {

    override fun createObservable(params: Long): Flow<RewatchStatus> =
        rewatchRepository.observeRewatchStatus(params)
}
