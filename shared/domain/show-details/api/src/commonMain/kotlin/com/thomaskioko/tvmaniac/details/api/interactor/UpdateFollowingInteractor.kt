package com.thomaskioko.tvmaniac.details.api.interactor

import com.thomaskioko.tvmaniac.core.util.FlowInteractor
import com.thomaskioko.tvmaniac.trakt.api.TraktRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UpdateFollowingInteractor constructor(
    private val traktRepository: TraktRepository
) : FlowInteractor<UpdateShowParams, Unit>() {

    override fun run(params: UpdateShowParams): Flow<Unit> =
        traktRepository.observeUpdateFollowedShow(
            showId = params.showId,
            addToWatchList = !params.addToWatchList
        ).map { it.data }
}

data class UpdateShowParams(
    val showId: Long,
    val addToWatchList: Boolean
)
