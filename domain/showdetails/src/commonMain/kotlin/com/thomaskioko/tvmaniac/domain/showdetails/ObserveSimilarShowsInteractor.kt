package com.thomaskioko.tvmaniac.domain.showdetails

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.showdetails.model.Show
import com.thomaskioko.tvmaniac.similar.api.SimilarShowsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Inject
public class ObserveSimilarShowsInteractor(
    private val similarShowsRepository: SimilarShowsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : SubjectInteractor<Long, List<Show>>() {

    override fun createObservable(params: Long): Flow<List<Show>> =
        similarShowsRepository.observeSimilarShows(params)
            .map { it.toSimilarShowList() }
            .flowOn(dispatchers.io)
}
