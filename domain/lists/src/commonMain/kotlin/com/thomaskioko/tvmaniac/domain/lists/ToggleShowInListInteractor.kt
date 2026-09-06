package com.thomaskioko.tvmaniac.domain.lists

import com.thomaskioko.tvmaniac.accountmanager.api.ProviderFeatures
import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.lists.api.ListRepository
import dev.zacsweers.metro.Inject

@Inject
public class ToggleShowInListInteractor(
    private val repository: ListRepository,
    private val userRepository: UserRepository,
    private val activeProviderFeatures: () -> ProviderFeatures,
) : Interactor<ToggleShowInListInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        val traktSlug = if (activeProviderFeatures().supportsLists) userRepository.getCurrentUser()?.slug else null
        repository.toggleShowInList(
            listId = params.listId,
            showId = params.showId,
            isCurrentlyInList = params.isCurrentlyInList,
            traktSlug = traktSlug,
        )
    }

    public data class Params(
        val listId: Long,
        val showId: Long,
        val isCurrentlyInList: Boolean,
    )
}
