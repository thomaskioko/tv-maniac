package com.thomaskioko.tvmaniac.domain.traktlists

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import dev.zacsweers.metro.Inject

@Inject
public class SyncTraktListsInteractor(
    private val repository: TraktListRepository,
    private val userRepository: UserRepository,
) : Interactor<SyncTraktListsInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        val slug = userRepository.getCurrentUser()?.slug ?: return
        repository.fetchUserLists(slug = slug, forceRefresh = params.forceRefresh)
    }

    public data class Params(val forceRefresh: Boolean = false)
}
