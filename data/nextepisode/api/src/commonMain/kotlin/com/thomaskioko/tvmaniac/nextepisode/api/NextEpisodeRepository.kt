package com.thomaskioko.tvmaniac.nextepisode.api

import com.thomaskioko.tvmaniac.nextepisode.api.model.NextEpisodeWithShow
import kotlinx.coroutines.flow.Flow

public interface NextEpisodeRepository {

    public suspend fun fetchNextEpisode(showId: Long)

    public fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>>

    public fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?>

    public suspend fun refreshNextEpisodeData(showId: Long)

    /**
     * Removes all next episode and watched episode data for a show when it's untracked.
     * This includes:
     * - Next episode cache data
     * - Watched episodes history
     *
     * @param showId The ID of the show to remove tracking data for
     */
    public suspend fun removeShowFromTracking(showId: Long)
}
