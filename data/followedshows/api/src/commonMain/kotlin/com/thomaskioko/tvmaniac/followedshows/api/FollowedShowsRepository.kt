package com.thomaskioko.tvmaniac.followedshows.api

import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public interface FollowedShowsRepository {
    public suspend fun getFollowedShows(): List<FollowedShowEntry>
    public suspend fun syncFollowedShows(forceRefresh: Boolean = false)
    public suspend fun addFollowedShow(traktId: Long)
    public suspend fun removeFollowedShow(traktId: Long)
    public suspend fun needsSync(expiry: Duration = 3.hours): Boolean
}
