package com.thomaskioko.tvmaniac.domain.traktlists

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class ToggleShowInListInteractor(
    private val repository: TraktListRepository,
) : Interactor<ToggleShowInListInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        repository.toggleShowInList(
            listId = params.listId,
            traktShowId = params.traktShowId,
            isCurrentlyInList = params.isCurrentlyInList,
        )
    }

    public data class Params(
        val listId: Long,
        val traktShowId: Long,
        val isCurrentlyInList: Boolean,
    )
}
