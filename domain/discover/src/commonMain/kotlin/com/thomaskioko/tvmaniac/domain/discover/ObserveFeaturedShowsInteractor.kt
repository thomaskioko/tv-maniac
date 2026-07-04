package com.thomaskioko.tvmaniac.domain.discover

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.featuredshows.api.FeaturedShowsRepository
import com.thomaskioko.tvmaniac.shows.api.model.ShowEntity
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveFeaturedShowsInteractor(
    private val repository: FeaturedShowsRepository,
) : SubjectInteractor<Unit, List<ShowEntity>>() {

    override fun createObservable(params: Unit): Flow<List<ShowEntity>> =
        repository.observeFeaturedShows()
}
