package com.thomaskioko.tvmaniac.data.ratings.api

import kotlinx.coroutines.flow.Flow

public interface RatingsRepository {
    public suspend fun rateShow(showId: Long, rating: Int)
    public suspend fun removeShowRating(showId: Long)
    public suspend fun syncPendingRatings()
    public suspend fun refreshCommunityRating(showId: Long, forceRefresh: Boolean)
    public fun observeShowRating(showId: Long): Flow<ShowRating>

    public suspend fun rateSeason(seasonId: Long, rating: Int)
    public suspend fun removeSeasonRating(seasonId: Long)
    public fun observeSeasonRating(seasonId: Long): Flow<SeasonRating>

    public suspend fun rateEpisode(episodeId: Long, rating: Int)
    public suspend fun removeEpisodeRating(episodeId: Long)
    public fun observeEpisodeRating(episodeId: Long): Flow<EpisodeRating>
}
