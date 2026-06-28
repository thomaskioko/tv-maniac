package com.thomaskioko.tvmaniac.followedshows.api

import kotlinx.coroutines.flow.Flow

public interface FollowedShowsRepository {
    public suspend fun getFollowedShows(): List<FollowedShowEntry>
    public suspend fun addFollowedShow(showId: Long)
    public suspend fun removeFollowedShow(showId: Long)
    public fun observeIsFollowed(showId: Long): Flow<Boolean>
}
