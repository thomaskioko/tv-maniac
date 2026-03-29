package com.thomaskioko.tvmaniac.domain.traktlists

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class SyncTraktListsInteractor(
    private val repository: TraktListRepository,
) : Interactor<SyncTraktListsInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        repository.syncLists(forceRefresh = params.forceRefresh)
    }

    public data class Params(val forceRefresh: Boolean = false)
}
