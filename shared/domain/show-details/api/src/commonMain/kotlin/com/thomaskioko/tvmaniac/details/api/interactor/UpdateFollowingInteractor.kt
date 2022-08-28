package com.thomaskioko.tvmaniac.details.api.interactor

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UpdateFollowingInteractor constructor(
    private val traktRepository: TraktRepository
) : FlowInteractor<UpdateShowParams, Unit>() {

    override fun run(params: UpdateShowParams): Flow<Unit> =
        if (params.isLoggedIn) {
            traktRepository.observeUpdateFollowedShow(
                showId = params.showId,
                addToWatchList = !params.addToWatchList
            ).map { }
        } else {
            flow {
                traktRepository.updateFollowedShow(
                    showId = params.showId,
                    addToWatchList = !params.addToWatchList
                )
                emit(Unit)
            }
        }
}

data class UpdateShowParams(
    val showId: Long,
    val addToWatchList: Boolean,
    val isLoggedIn: Boolean
)
