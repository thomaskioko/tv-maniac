package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject

@Inject
public class RefreshCommunityRatingInteractor(
    private val ratingsRepository: RatingsRepository,
) : Interactor<RefreshCommunityRatingInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        ratingsRepository.refreshCommunityRating(params.showId, params.forceRefresh)
    }

    public data class Param(
        val showId: Long,
        val forceRefresh: Boolean = false,
    )
}
