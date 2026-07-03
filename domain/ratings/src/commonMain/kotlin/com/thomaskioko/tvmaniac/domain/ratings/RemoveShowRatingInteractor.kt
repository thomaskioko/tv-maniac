package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject

@Inject
public class RemoveShowRatingInteractor(
    private val ratingsRepository: RatingsRepository,
) : Interactor<Long>() {

    override suspend fun doWork(params: Long) {
        ratingsRepository.removeShowRating(params)
    }
}
