package com.thomaskioko.tvmaniac.domain.discover

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.discover.api.TrendingShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveTrendingShowsInteractor(
    private val repository: TrendingShowsRepository,
) : SubjectInteractor<Unit, List<ShowEntity>>() {

    override fun createObservable(params: Unit): Flow<List<ShowEntity>> =
        repository.observeTrendingShows()
}
