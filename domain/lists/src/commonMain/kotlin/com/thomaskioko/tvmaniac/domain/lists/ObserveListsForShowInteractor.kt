package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import com.thomaskioko.tvmaniac.lists.api.UserList
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveListsForShowInteractor(
    private val repository: ListRepository,
) : SubjectInteractor<Long, List<UserList>>() {

    override fun createObservable(params: Long): Flow<List<UserList>> =
        repository.observeListsForShow(params)
}
