package com.thomaskioko.tvmaniac.domain.ratings

import com.thomaskioko.tvmaniac.core.base.interactor.SubjectInteractor
import com.thomaskioko.tvmaniac.data.ratings.api.CommunityRating
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
public class ObserveCommunityRatingInteractor(
    private val ratingsRepository: RatingsRepository,
) : SubjectInteractor<Long, CommunityRating?>() {

    override fun createObservable(params: Long): Flow<CommunityRating?> =
        ratingsRepository.observeShowRating(params).map { rating ->
            rating.communityRating?.let { CommunityRating(rating = it, votes = rating.communityVotes ?: 0L) }
        }
}
