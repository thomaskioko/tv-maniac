package com.thomaskioko.tvmaniac.syncactivity.testing

import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.implementation.DefaultActivitySyncRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class, replaces = [DefaultActivitySyncRepository::class])
public class FakeActivitySyncRepository : ActivitySyncRepository {

    private val remoteTimestamps = mutableMapOf<ActivityType, Instant>()
    private val checkpoints = mutableMapOf<Pair<String, ActivityType>, Instant>()
    private val markSyncedToInvocations = mutableListOf<Pair<String, ActivityType>>()
    private var clearAllInvocationCount = 0

    public fun setRemoteTimestamp(activityType: ActivityType, instant: Instant?) {
        if (instant == null) {
            remoteTimestamps.remove(activityType)
        } else {
            remoteTimestamps[activityType] = instant
        }
    }

    public fun setCheckpoint(consumerId: String, activityType: ActivityType, instant: Instant?) {
        val key = consumerId to activityType
        if (instant == null) {
            checkpoints.remove(key)
        } else {
            checkpoints[key] = instant
        }
    }

    public fun markSyncedToCalls(): List<Pair<String, ActivityType>> = markSyncedToInvocations.toList()

    public fun clearAllCallCount(): Int = clearAllInvocationCount

    override suspend fun isAheadOf(consumerId: String, activityType: ActivityType): Boolean {
        val remote = remoteTimestamps[activityType] ?: return false
        val synced = checkpoints[consumerId to activityType]
        return synced == null || remote > synced
    }

    override suspend fun markSyncedTo(consumerId: String, activityType: ActivityType) {
        markSyncedToInvocations.add(consumerId to activityType)
        val remote = remoteTimestamps[activityType] ?: return
        checkpoints[consumerId to activityType] = remote
    }

    override suspend fun getSyncTimestamp(consumerId: String, activityType: ActivityType): Instant? =
        checkpoints[consumerId to activityType]

    override suspend fun clearAll() {
        clearAllInvocationCount++
        checkpoints.clear()
    }
}
