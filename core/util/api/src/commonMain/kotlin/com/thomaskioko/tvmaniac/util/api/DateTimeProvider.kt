package com.thomaskioko.tvmaniac.util.api

import kotlinx.datetime.TimeZone
import kotlin.time.Instant

public interface DateTimeProvider {
    public fun now(): Instant
    public fun nowMillis(): Long = now().toEpochMilliseconds()
    public fun startOfDay(timeZone: TimeZone = TimeZone.currentSystemDefault()): Instant
    public fun epochToIsoDate(epochMillis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String
    public fun epochToDisplayDateTime(epochMillis: Long, timeZone: TimeZone = TimeZone.currentSystemDefault()): String
    public fun extractYear(dateString: String): String
    public fun todayAsIsoDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): String
    public fun isoDateToEpoch(dateStr: String?): Long?
    public fun currentYear(timeZone: TimeZone = TimeZone.currentSystemDefault()): Int
}
