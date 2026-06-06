package com.thomaskioko.tvmaniac.followedshows.api

public interface FollowedShowsRepository {
    public suspend fun getFollowedShows(): List<FollowedShowEntry>
    public suspend fun addFollowedShow(showId: Long)
    public suspend fun removeFollowedShow(showId: Long)
}
