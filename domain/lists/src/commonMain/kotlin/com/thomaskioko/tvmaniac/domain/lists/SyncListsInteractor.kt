package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import dev.zacsweers.metro.Inject

@Inject
public class SyncListsInteractor(
    private val repository: ListRepository,
    private val userRepository: UserRepository,
    private val activeProviderFeatures: () -> ProviderFeatures,
) : Interactor<SyncListsInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        if (!activeProviderFeatures().supportsLists) return
        val slug = userRepository.getCurrentUser()?.slug ?: return
        repository.fetchUserLists(slug = slug, forceRefresh = params.forceRefresh)
    }

    public data class Params(val forceRefresh: Boolean = false)
}
