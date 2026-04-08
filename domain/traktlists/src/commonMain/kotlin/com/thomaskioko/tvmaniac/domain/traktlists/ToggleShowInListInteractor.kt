package com.thomaskioko.tvmaniac.domain.traktlists

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.user.api.UserRepository
import com.thomaskioko.tvmaniac.traktlists.api.TraktListRepository
import me.tatarka.inject.annotations.Inject

@Inject
public class ToggleShowInListInteractor(
    private val repository: TraktListRepository,
    private val userRepository: UserRepository,
) : Interactor<ToggleShowInListInteractor.Params>() {

    override suspend fun doWork(params: Params) {
        val slug = userRepository.getCurrentUser()?.slug ?: return
        repository.toggleShowInList(
            slug = slug,
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
