package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject

@Inject
public class FetchRateSeasonInteractor(
    private val ratingsRepository: RatingsRepository,
) : Interactor<FetchRateSeasonInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        ratingsRepository.rateSeason(params.seasonId, params.rating)
    }

    public data class Param(
        val seasonId: Long,
        val rating: Int,
    )
}
