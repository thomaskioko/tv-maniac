package com.thomaskioko.tvmaniac.syncactivity.api

import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.api.model.TraktLastActivity
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

public interface TraktActivityDao {
    public fun upsert(activityType: ActivityType, remoteTimestamp: Instant, fetchedAt: Instant)
    public fun getByActivityType(activityType: ActivityType): TraktLastActivity?
    public fun isDurationExpired(activityType: ActivityType): Boolean
    public fun markAsSynced(activityType: ActivityType)
    public fun observeAll(): Flow<List<TraktLastActivity>>
    public fun getAll(): List<TraktLastActivity>
    public fun delete(activityType: ActivityType)
    public fun deleteAll()
}
