package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject

@Inject
public class RateInteractor(
    private val ratingsRepository: RatingsRepository,
) : Interactor<RateInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        when (params.type) {
            RatingEntityType.SHOW -> ratingsRepository.rateShow(params.id, params.rating)
            RatingEntityType.SEASON -> ratingsRepository.rateSeason(params.id, params.rating)
            RatingEntityType.EPISODE -> ratingsRepository.rateEpisode(params.id, params.rating)
        }
    }

    public data class Param(
        val type: RatingEntityType,
        val id: Long,
        val rating: Int,
    )
}
