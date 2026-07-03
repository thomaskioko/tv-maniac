package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRating
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObservableShowRatingInteractor(
    private val ratingsRepository: RatingsRepository,
) : SubjectInteractor<Long, ShowRating>() {

    override fun createObservable(params: Long): Flow<ShowRating> = ratingsRepository.observeShowRating(params)
}
