package com.thomaskioko.tvmaniac.domain.seasondetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsInteractor.Param
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext

@Inject
class SeasonDetailsInteractor(
    private val seasonDetailsRepository: SeasonDetailsRepository,
    private val dispatchers: AppCoroutineDispatchers,
) : Interactor<Param>() {
    override suspend fun doWork(params: Param) {
        withContext(dispatchers.io) {
            seasonDetailsRepository.fetchSeasonDetails(
                param = params.seasonDetails,
                forceRefresh = params.forceRefresh,
            )
        }
    }

    data class Param(val seasonDetails: SeasonDetailsParam, val forceRefresh: Boolean = false)
}
