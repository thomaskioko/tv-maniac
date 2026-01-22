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
    private var epochToIsoDateResult: String = "2024-01-01"
    private var epochToDisplayDateTimeResult: String = "2024-01-01 12:00"
    private var extractYearResult: String = "2024"
    private var fakeToday: LocalDate? = null
    private var currentYearResult: Int = 2024

    override fun now(): Instant = currentTime

    private fun today(timeZone: TimeZone): LocalDate = fakeToday ?: currentTime.toLocalDateTime(timeZone).date

    override fun startOfDay(timeZone: TimeZone): Instant =
        currentTime.toLocalDateTime(timeZone).date.atStartOfDayIn(timeZone)

    override fun epochToIsoDate(epochMillis: Long, timeZone: TimeZone): String = epochToIsoDateResult
    override fun epochToDisplayDateTime(epochMillis: Long, timeZone: TimeZone): String = epochToDisplayDateTimeResult
    override fun extractYear(dateString: String): String = extractYearResult
    override fun todayAsIsoDate(timeZone: TimeZone): String = today(timeZone).toString()

    override fun isoDateToEpoch(dateStr: String?): Long? {
        if (dateStr.isNullOrBlank()) return null
        return runCatching { Instant.parse(dateStr).toEpochMilliseconds() }
            .getOrNull()
    }

    override fun currentYear(timeZone: TimeZone): Int = currentYearResult

    public fun setCurrentTime(instant: Instant) {
        currentTime = instant
    }

    public fun setCurrentTimeMillis(millis: Long) {
        currentTime = Instant.fromEpochMilliseconds(millis)
    }

    public fun setFakeToday(year: Int, month: Int, day: Int) {
        fakeToday = LocalDate(year, month, day)
    }

    public fun setEpochToIsoDateResult(result: String) {
        epochToIsoDateResult = result
    }

    public fun setEpochToDisplayDateTimeResult(result: String) {
        epochToDisplayDateTimeResult = result
    }

    public fun setExtractYearResult(result: String) {
        extractYearResult = result
    }

    public fun setCurrentYear(year: Int) {
        currentYearResult = year
    }
}
