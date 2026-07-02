package com.thomaskioko.tvmaniac.data.ratings.testing

import com.thomaskioko.tvmaniac.data.ratings.api.EpisodeRating
import com.thomaskioko.tvmaniac.data.ratings.api.RatingsRepository
import com.thomaskioko.tvmaniac.data.ratings.api.SeasonRating
import com.thomaskioko.tvmaniac.data.ratings.api.ShowRating
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeRatingsRepository : RatingsRepository {

    private var syncPendingRatingsError: Throwable? = null
    private val showRatingFlow = MutableStateFlow(
        ShowRating(userRating = null, communityRating = null, communityVotes = null, pendingAction = PendingAction.NOTHING),
    )
    private val seasonRatingFlow = MutableStateFlow(
        SeasonRating(userRating = null, pendingAction = PendingAction.NOTHING),
    )
    private val episodeRatingFlow = MutableStateFlow(
        EpisodeRating(userRating = null, pendingAction = PendingAction.NOTHING),
    )

    public fun setSyncPendingRatingsError(error: Throwable?) {
        syncPendingRatingsError = error
    }

    public fun setShowRating(rating: ShowRating) {
        showRatingFlow.value = rating
    }

    public fun setSeasonRating(rating: SeasonRating) {
        seasonRatingFlow.value = rating
    }

    public fun setEpisodeRating(rating: EpisodeRating) {
        episodeRatingFlow.value = rating
    }

    override suspend fun rateShow(showId: Long, rating: Int) {
    }

    override suspend fun removeShowRating(showId: Long) {
    }

    override suspend fun syncPendingRatings() {
        syncPendingRatingsError?.let { throw it }
    }

    override suspend fun refreshCommunityRating(showId: Long) {
    }

    override fun observeShowRating(showId: Long): Flow<ShowRating> = showRatingFlow.asStateFlow()

    override suspend fun rateSeason(seasonId: Long, rating: Int) {
    }

    override suspend fun removeSeasonRating(seasonId: Long) {
    }

    override fun observeSeasonRating(seasonId: Long): Flow<SeasonRating> = seasonRatingFlow.asStateFlow()

    override suspend fun rateEpisode(episodeId: Long, rating: Int) {
    }

    override suspend fun removeEpisodeRating(episodeId: Long) {
    }

    override fun observeEpisodeRating(episodeId: Long): Flow<EpisodeRating> = episodeRatingFlow.asStateFlow()
}
