package com.thomaskioko.tvmaniac.syncactivity.testing

import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.implementation.DefaultTraktActivityRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultTraktActivityRepository::class])
public class FakeTraktActivityRepository : TraktActivityRepository {

    private val changedActivities = mutableSetOf<ActivityType>()
    private val syncedActivities = mutableSetOf<ActivityType>()

    public fun setActivityChanged(activityType: ActivityType, changed: Boolean) {
        if (changed) {
            changedActivities.add(activityType)
        } else {
            changedActivities.remove(activityType)
        }
    }

    public fun getSyncedActivities(): Set<ActivityType> = syncedActivities.toSet()

    override suspend fun fetchLatestActivities(forceRefresh: Boolean) {}

    override suspend fun hasActivityChanged(activityType: ActivityType): Boolean =
        changedActivities.contains(activityType)

    override suspend fun markActivityAsSynced(activityType: ActivityType) {
        syncedActivities.add(activityType)
        changedActivities.remove(activityType)
    }

    override suspend fun clearAllActivities() {
        changedActivities.clear()
        syncedActivities.clear()
    }
}
