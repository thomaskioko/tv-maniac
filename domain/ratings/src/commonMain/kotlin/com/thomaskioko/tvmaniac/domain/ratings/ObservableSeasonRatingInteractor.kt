package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import com.thomaskioko.tvmaniac.data.ratings.api.SeasonRating
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObservableSeasonRatingInteractor(
    private val ratingsRepository: RatingsRepository,
) : SubjectInteractor<Long, SeasonRating>() {

    override fun createObservable(params: Long): Flow<SeasonRating> = ratingsRepository.observeSeasonRating(params)
}
