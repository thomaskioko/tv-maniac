package com.thomaskioko.tvmaniac.util.api

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

public interface DateTimeProvider {
    public fun now(): Instant
    public fun nowMillis(): Long = now().toEpochMilliseconds()
    public fun today(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate
    public fun calculateDaysUntilAir(airDateStr: String?, timeZone: TimeZone = TimeZone.currentSystemDefault()): Int?
}
