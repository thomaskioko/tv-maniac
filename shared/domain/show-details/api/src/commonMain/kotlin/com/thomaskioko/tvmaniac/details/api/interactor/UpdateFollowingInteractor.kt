package com.thomaskioko.tvmaniac.details.api.interactor

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.details.api.repository.TvShowsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateFollowingInteractor constructor(
    private val repository: TvShowsRepository,
) : FlowInteractor<UpdateShowParams, Unit>() {

    override fun run(params: UpdateShowParams): Flow<Unit> = flow {
        repository.updateFollowing(
            showId = params.showId,
            addToWatchList = params.addToWatchList
        )
        emit(Unit)
    }
}

data class UpdateShowParams(
    val showId: Long,
    val addToWatchList: Boolean
)
