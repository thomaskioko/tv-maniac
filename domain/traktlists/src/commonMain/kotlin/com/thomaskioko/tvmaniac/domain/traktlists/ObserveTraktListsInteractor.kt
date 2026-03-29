package com.thomaskioko.tvmaniac.domain.traktlists

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import com.thomaskioko.tvmaniac.traktlists.api.TraktListWithMembership
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveTraktListsInteractor(
    private val repository: TraktListRepository,
) : SubjectInteractor<Long, List<TraktListWithMembership>>() {

    override fun createObservable(params: Long): Flow<List<TraktListWithMembership>> =
        repository.observeListsForShow(params)
}
