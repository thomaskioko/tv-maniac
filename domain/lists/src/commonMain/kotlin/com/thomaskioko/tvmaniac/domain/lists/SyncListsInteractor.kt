package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import dev.zacsweers.metro.Inject

@Inject
public class SyncListsInteractor(
    private val repository: ListRepository,
    private val userRepository: UserRepository,
) : Interactor<SyncListsInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        val slug = userRepository.getCurrentUser()?.slug ?: return
        repository.fetchUserLists(slug = slug, forceRefresh = params.forceRefresh)
    }

    public data class Params(val forceRefresh: Boolean = false)
}
