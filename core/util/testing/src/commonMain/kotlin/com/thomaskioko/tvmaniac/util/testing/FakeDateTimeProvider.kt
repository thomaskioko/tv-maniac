package com.thomaskioko.tvmaniac.util.testing

import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

public class FakeDateTimeProvider(
    private var currentTime: Instant = Clock.System.now(),
) : DateTimeProvider {
    override fun now(): Instant = currentTime
    override fun today(timeZone: TimeZone): LocalDate = currentTime.toLocalDateTime(timeZone).date
    override fun calculateDaysUntilAir(airDateStr: String?, timeZone: TimeZone): Int? = null

    public fun setCurrentTime(instant: Instant) {
        currentTime = instant
    }

    public fun setCurrentTimeMillis(millis: Long) {
        currentTime = Instant.fromEpochMilliseconds(millis)
    }
}
