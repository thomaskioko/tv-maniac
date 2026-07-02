package com.thomaskioko.tvmaniac.data.ratings.api

import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import kotlinx.coroutines.flow.Flow

public interface RatingsDao {
    public fun upsertShowUserRating(showId: Long, userRating: Long, ratedAt: Long, pendingAction: PendingAction)
    public fun observeShowRating(showId: Long): Flow<ShowRatingEntry?>
    public fun clearShowUserRating(showId: Long)
    public fun showRatingsWithUploadPendingAction(): List<ShowRatingEntry>
    public fun showRatingsWithDeletePendingAction(): List<ShowRatingEntry>
    public fun updateShowRatingPendingAction(showId: Long, action: PendingAction)
    public fun deleteShowRating(showId: Long)

    public fun upsertSeasonUserRating(seasonId: Long, userRating: Long, ratedAt: Long, pendingAction: PendingAction)
    public fun observeSeasonRating(seasonId: Long): Flow<SeasonRatingEntry?>
    public fun clearSeasonUserRating(seasonId: Long)
    public fun seasonRatingsWithUploadPendingAction(): List<SeasonRatingEntry>
    public fun seasonRatingsWithDeletePendingAction(): List<SeasonRatingEntry>
    public fun updateSeasonRatingPendingAction(seasonId: Long, action: PendingAction)
    public fun deleteSeasonRating(seasonId: Long)

    public fun upsertEpisodeUserRating(episodeId: Long, userRating: Long, ratedAt: Long, pendingAction: PendingAction)
    public fun observeEpisodeRating(episodeId: Long): Flow<EpisodeRatingEntry?>
    public fun clearEpisodeUserRating(episodeId: Long)
    public fun episodeRatingsWithUploadPendingAction(): List<EpisodeRatingEntry>
    public fun episodeRatingsWithDeletePendingAction(): List<EpisodeRatingEntry>
    public fun updateEpisodeRatingPendingAction(episodeId: Long, action: PendingAction)
    public fun deleteEpisodeRating(episodeId: Long)

    public fun clearAll()
}
