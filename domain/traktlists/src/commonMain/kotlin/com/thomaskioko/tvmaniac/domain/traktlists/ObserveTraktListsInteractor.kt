package com.thomaskioko.tvmaniac.domain.traktlists

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.traktlists.api.TraktListEntity
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
public class ObserveTraktListsInteractor(
    private val repository: TraktListRepository,
) : SubjectInteractor<Unit, List<TraktListEntity>>() {

    override fun createObservable(params: Unit): Flow<List<TraktListEntity>> =
        repository.observeLists()
}
