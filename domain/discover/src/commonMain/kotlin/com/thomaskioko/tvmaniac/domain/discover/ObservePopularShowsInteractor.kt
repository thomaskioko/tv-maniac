package com.thomaskioko.tvmaniac.domain.discover

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.popularshows.api.PopularShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObservePopularShowsInteractor(
    private val repository: PopularShowsRepository,
) : SubjectInteractor<Unit, List<ShowEntity>>() {

    override fun createObservable(params: Unit): Flow<List<ShowEntity>> =
        repository.observePopularShows()
}
