package com.thomaskioko.tvmaniac.domain.discover

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import com.thomaskioko.tvmaniac.topratedshows.data.api.TopRatedShowsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveTopRatedShowsInteractor(
    private val repository: TopRatedShowsRepository,
) : SubjectInteractor<Unit, List<ShowEntity>>() {

    override fun createObservable(params: Unit): Flow<List<ShowEntity>> =
        repository.observeTopRatedShows()
}
