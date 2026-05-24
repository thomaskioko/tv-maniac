package com.thomaskioko.tvmaniac.syncactivity.api

import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import kotlin.time.Instant

/**
 * Tracks per-consumer sync positions for Trakt activity timestamps.
 *
 * Replaces the shared `trakt_last_activity.synced_remote_timestamp` column,
 * which had multiple writers and let one consumer's mark suppress every
 * other consumer's read. Each consumer entry tracks its own "synced up to" position
 * against Trakt's `remote_timestamp` for a given [ActivityType], so consumers never
 * block each other's progress.
 */
public interface ActivitySyncRepository {

    /**
     * Whether Trakt's remote timestamp for [activityType] is ahead of the
     * consumer's checkpoint.
     *
     * Returns true when the consumer has no checkpoint yet (first sync).
     * Returns false when Trakt has no remote timestamp recorded for this
     * activity (activity has never been fetched); callers must run the
     * activity fetch before consulting the gate.
     */
    public suspend fun isAheadOf(consumerId: String, activityType: ActivityType): Boolean

    /**
     * Records [consumerId] as synced up to Trakt's current remote
     * timestamp for [activityType]. No-op when no remote timestamp is
     * recorded.
     */
    public suspend fun markSyncedTo(consumerId: String, activityType: ActivityType)

    /**
     * Last-synced timestamp for [consumerId] against [activityType], or
     * null when no checkpoint exists. Used as a Trakt query cursor by
     * consumers that need an incremental "since" parameter.
     */
    public suspend fun getSyncTimestamp(consumerId: String, activityType: ActivityType): Instant?

    /**
     * Removes every consumer checkpoint. Called from `LogoutInteractor`
     * so an account switch does not carry the previous account's
     * positions into the new account's sync.
     */
    public suspend fun clearAll()
}
