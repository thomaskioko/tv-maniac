package com.thomaskioko.tvmaniac.episodes.api

import kotlin.time.Instant

public object WatchedDate {

    public const val UNKNOWN_MILLIS: Long = 0L

    public val UNKNOWN: Instant = Instant.fromEpochMilliseconds(UNKNOWN_MILLIS)

    public fun isUnknown(epochMillis: Long): Boolean = epochMillis <= UNKNOWN_MILLIS

    public fun isUnknown(instant: Instant): Boolean = isUnknown(instant.toEpochMilliseconds())

    public fun normalize(instant: Instant): Instant = if (isUnknown(instant)) UNKNOWN else instant

    public fun releaseDateMillis(firstAired: Long?, runtimeMinutes: Long?): Long? {
        if (firstAired == null) return null
        return firstAired + (runtimeMinutes?.takeIf { it > 0 } ?: 0L) * MILLIS_PER_MINUTE
    }

    private const val MILLIS_PER_MINUTE = 60_000L
}
