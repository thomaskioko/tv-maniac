package com.thomaskioko.tvmaniac.watchedshows.api

public interface WatchedShowsRepository {

    public suspend fun syncWatchedShows(forceRefresh: Boolean = false)
}
