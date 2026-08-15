package com.thomaskioko.tvmaniac.episodes.api

import kotlin.time.Instant

public object WatchedDate {

    public const val UNKNOWN_MILLIS: Long = 0L

    public val UNKNOWN: Instant = Instant.fromEpochMilliseconds(UNKNOWN_MILLIS)

    public fun isUnknown(epochMillis: Long): Boolean = epochMillis <= UNKNOWN_MILLIS

    public fun isUnknown(instant: Instant): Boolean = isUnknown(instant.toEpochMilliseconds())

    public fun normalize(instant: Instant): Instant = if (isUnknown(instant)) UNKNOWN else instant
}
