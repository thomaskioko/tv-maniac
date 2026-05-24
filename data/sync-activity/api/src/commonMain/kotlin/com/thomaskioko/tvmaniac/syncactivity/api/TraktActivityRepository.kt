package com.thomaskioko.tvmaniac.syncactivity.api

public interface TraktActivityRepository {
    public suspend fun fetchLatestActivities(forceRefresh: Boolean)
    public suspend fun clearAllActivities()
}
