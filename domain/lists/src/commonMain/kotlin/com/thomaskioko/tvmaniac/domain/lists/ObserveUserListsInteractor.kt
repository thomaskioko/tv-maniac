package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import com.thomaskioko.tvmaniac.lists.api.UserListEntity
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObserveUserListsInteractor(
    private val repository: ListRepository,
) : SubjectInteractor<Unit, List<UserListEntity>>() {

    override fun createObservable(params: Unit): Flow<List<UserListEntity>> =
        repository.observeLists()
}
