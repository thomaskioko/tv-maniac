package com.thomaskioko.tvmaniac.syncactivity.api

import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import kotlin.time.Instant

/**
 * Tracks per-consumer sync positions for activity timestamps, scoped to the active provider.
 *
 * Replaces the shared `trakt_last_activity.synced_remote_timestamp` column, which had multiple writers
 * and let one consumer's mark suppress every other consumer's read. Each entry tracks its own "synced
 * up to" position against the remote `remote_timestamp` for a given [ActivityType]. The implementation
 * keys checkpoints on the active connected provider, so Trakt and a future Simkl account never collide;
 * callers never pass a provider, and adding a provider needs no call-site change.
 */
public interface ActivitySyncRepository {

    /**
     * Whether the remote timestamp for [activityType] is ahead of the active provider's checkpoint for
     * [consumerId].
     *
     * Returns true when no checkpoint exists yet (first sync). Returns false when no provider is
     * connected, or when no remote timestamp is recorded for this activity (never fetched); callers must
     * run the activity fetch before consulting the gate.
     */
    public suspend fun isAheadOf(consumerId: String, activityType: ActivityType): Boolean

    /**
     * Records [consumerId] as synced up to the current remote timestamp for [activityType], under the
     * active provider. No-op when no provider is connected or no remote timestamp is recorded.
     */
    public suspend fun markSyncedTo(consumerId: String, activityType: ActivityType)

    /**
     * Last-synced timestamp for the active provider's [consumerId] checkpoint against [activityType], or
     * null when no provider is connected or no checkpoint exists. Used as a query cursor by consumers
     * that need an incremental "since" parameter.
     */
    public suspend fun getSyncTimestamp(consumerId: String, activityType: ActivityType): Instant?

    /**
     * Removes every checkpoint across all providers. Called from `LogoutInteractor` so an account switch
     * does not carry the previous account's positions into the new account's sync.
     */
    public suspend fun clearAll()
}
