package com.thomaskioko.tvmaniac.domain.traktlists

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.traktlists.api.TraktList
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveTraktListsInteractor(
    private val repository: TraktListRepository,
) : SubjectInteractor<Long, List<TraktList>>() {

    override fun createObservable(params: Long): Flow<List<TraktList>> =
        repository.observeListsForShow(params)
}
