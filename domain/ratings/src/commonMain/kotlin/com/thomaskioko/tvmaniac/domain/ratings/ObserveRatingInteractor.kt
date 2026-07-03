package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingEntityType
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
public class ObserveRatingInteractor(
    private val ratingsRepository: RatingsRepository,
) : SubjectInteractor<ObserveRatingInteractor.Param, Int?>() {

    override fun createObservable(params: Param): Flow<Int?> = when (params.type) {
        RatingEntityType.SHOW -> ratingsRepository.observeShowRating(params.id).map { it.userRating }
        RatingEntityType.SEASON -> ratingsRepository.observeSeasonRating(params.id).map { it.userRating }
        RatingEntityType.EPISODE -> ratingsRepository.observeEpisodeRating(params.id).map { it.userRating }
    }

    public data class Param(
        val type: RatingEntityType,
        val id: Long,
    )
}
