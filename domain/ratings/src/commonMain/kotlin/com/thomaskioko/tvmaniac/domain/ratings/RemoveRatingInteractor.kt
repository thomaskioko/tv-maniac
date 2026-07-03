package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject

@Inject
public class RemoveRatingInteractor(
    private val ratingsRepository: RatingsRepository,
) : Interactor<RemoveRatingInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        when (params.type) {
            RatingEntityType.SHOW -> ratingsRepository.removeShowRating(params.id)
            RatingEntityType.SEASON -> ratingsRepository.removeSeasonRating(params.id)
            RatingEntityType.EPISODE -> ratingsRepository.removeEpisodeRating(params.id)
        }
    }

    public data class Param(
        val type: RatingEntityType,
        val id: Long,
    )
}
