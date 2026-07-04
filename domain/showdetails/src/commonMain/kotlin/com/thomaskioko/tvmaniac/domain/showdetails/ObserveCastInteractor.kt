package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.data.cast.api.CastRepository
import com.thomaskioko.tvmaniac.domain.showdetails.model.Casts
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Inject
public class ObserveCastInteractor(
    private val castRepository: CastRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Long, List<Casts>>() {

    override fun createObservable(params: Long): Flow<List<Casts>> =
        castRepository.observeShowCast(params)
            .map { it.toCastList() }
            .flowOn(dispatchers.io)
}
