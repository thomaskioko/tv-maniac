package com.thomaskioko.tvmaniac.util.testing

import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

public class FakeDateTimeProvider(
    private var currentTime: Instant = Clock.System.now(),
) : DateTimeProvider {
    public var formatDateResult: String = "2024-01-01"
    public var formatDateTimeResult: String = "2024-01-01 12:00"
    public var getYearResult: String = "2024"

    override fun now(): Instant = currentTime
    override fun today(timeZone: TimeZone): LocalDate = currentTime.toLocalDateTime(timeZone).date
    override fun calculateDaysUntilAir(airDateStr: String?, timeZone: TimeZone): Int? = null
    override fun startOfDay(timeZone: TimeZone): Instant =
        currentTime.toLocalDateTime(timeZone).date.atStartOfDayIn(timeZone)

    override fun formatDate(epochMillis: Long, timeZone: TimeZone): String = formatDateResult
    override fun formatDateTime(epochMillis: Long, timeZone: TimeZone): String = formatDateTimeResult
    override fun getYear(dateString: String): String = getYearResult

    public fun setCurrentTime(instant: Instant) {
        currentTime = instant
    }

    public fun setCurrentTimeMillis(millis: Long) {
        currentTime = Instant.fromEpochMilliseconds(millis)
    }
}
