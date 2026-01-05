package com.thomaskioko.tvmaniac.followedshows.api

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public interface FollowedShowsRepository {
    public fun observeFollowedShows(): Flow<List<FollowedShowEntry>>
    public suspend fun syncFollowedShows(forceRefresh: Boolean = false)
    public suspend fun addFollowedShow(tmdbId: Long)
    public suspend fun removeFollowedShow(tmdbId: Long)
    public suspend fun needsSync(expiry: Duration = 3.hours): Boolean
}
