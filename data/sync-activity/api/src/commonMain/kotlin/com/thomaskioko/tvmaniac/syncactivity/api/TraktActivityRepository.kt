package com.thomaskioko.tvmaniac.syncactivity.api

import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import kotlin.time.Instant

public interface TraktActivityRepository {
    public suspend fun fetchLatestActivities(forceRefresh: Boolean)
    public suspend fun hasActivityChanged(activityType: ActivityType): Boolean
    public suspend fun markActivityAsSynced(activityType: ActivityType)
    public suspend fun clearAllActivities()
    public suspend fun getEpisodesWatchedSyncTimeStamp(): Instant?
}
