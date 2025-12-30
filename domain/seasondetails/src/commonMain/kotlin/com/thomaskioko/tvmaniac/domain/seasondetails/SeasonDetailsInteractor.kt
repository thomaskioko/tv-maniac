package com.thomaskioko.tvmaniac.domain.seasondetails

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.seasondetails.SeasonDetailsInteractor.Param
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsParam
import com.thomaskioko.tvmaniac.seasondetails.api.SeasonDetailsRepository
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@Inject
public class SeasonDetailsInteractor(
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

    public data class Param(val seasonDetails: SeasonDetailsParam, val forceRefresh: Boolean = false)
}
