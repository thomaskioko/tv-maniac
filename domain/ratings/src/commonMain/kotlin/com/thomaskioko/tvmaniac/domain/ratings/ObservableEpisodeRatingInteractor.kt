package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.ratings.api.EpisodeRating
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
public class ObservableEpisodeRatingInteractor(
    private val ratingsRepository: RatingsRepository,
) : SubjectInteractor<Long, EpisodeRating>() {

    override fun createObservable(params: Long): Flow<EpisodeRating> = ratingsRepository.observeEpisodeRating(params)
}
