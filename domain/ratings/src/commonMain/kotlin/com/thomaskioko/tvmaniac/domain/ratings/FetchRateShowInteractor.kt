package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject

@Inject
public class FetchRateShowInteractor(
    private val ratingsRepository: RatingsRepository,
) : Interactor<FetchRateShowInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        ratingsRepository.rateShow(params.showId, params.rating)
    }

    public data class Param(
        val showId: Long,
        val rating: Int,
    )
}
