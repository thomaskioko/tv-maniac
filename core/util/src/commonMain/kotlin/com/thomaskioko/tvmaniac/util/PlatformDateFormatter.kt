package com.thomaskioko.tvmaniac.util

import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

public const val DATE_YYYY_MM_DD_PATTERN: String = "yyyy-MM-dd"

public val startOfDay: Instant = Clock.System.now().toLocalDateTime(TimeZone.UTC).date.atStartOfDayIn(TimeZone.UTC)

public expect class PlatformDateFormatter() {
    /**
     * Returns the current time in milliseconds
     *
     * @return timestamp
     */
    public fun getTimestampMilliseconds(): Long

    /**
     * Returns the formatted date string "2023-01-12"
     *
     * @param epochMillis epoch time in milliseconds
     * @return String formatted date
     */
    public fun formatDate(epochMillis: Long): String

    /**
     * Returns the formatted date string "2023"
     *
     * @param dateString date string to be formatted
     */
    public fun getYear(dateString: String): String
}
