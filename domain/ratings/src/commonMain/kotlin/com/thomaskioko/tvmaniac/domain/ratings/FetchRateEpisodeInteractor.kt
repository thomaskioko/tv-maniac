package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.Interactor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject

@Inject
public class FetchRateEpisodeInteractor(
    private val ratingsRepository: RatingsRepository,
) : Interactor<FetchRateEpisodeInteractor.Param>() {

    override suspend fun doWork(params: Param) {
        ratingsRepository.rateEpisode(params.episodeId, params.rating)
    }

    public data class Param(
        val episodeId: Long,
        val rating: Int,
    )
}
