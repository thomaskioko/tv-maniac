package com.thomaskioko.tvmaniac.util

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultDateTimeProvider(
    private val formatterUtil: FormatterUtil,
    private val clock: Clock,
) : DateTimeProvider {
    override fun now(): Instant = clock.now()

    private fun today(timeZone: TimeZone): LocalDate = clock.todayIn(timeZone)

    override fun startOfDay(timeZone: TimeZone): Instant = now()
        .toLocalDateTime(timeZone)
        .date
        .atStartOfDayIn(timeZone)

    override fun epochToIsoDate(epochMillis: Long, timeZone: TimeZone): String {
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val localDate = instant.toLocalDateTime(timeZone).date
        return localDate.toString()
    }

    override fun epochToDisplayDateTime(epochMillis: Long, timeZone: TimeZone): String {
        return formatterUtil.formatDateTime(epochMillis, "MMM d, yyyy 'at' HH:mm")
    }

    override fun extractYear(dateString: String): String {
        if (dateString.isEmpty()) return "--"
        return runCatching { LocalDate.parse(dateString).year }
            .recoverCatching { Instant.parse(dateString).toLocalDateTime(TimeZone.UTC).year }
            .map { it.toString() }
            .getOrElse { exception ->
                Logger.e("extractYear:: $dateString ${exception.message}", exception)
                "TBA"
            }
    }

    override fun todayAsIsoDate(timeZone: TimeZone): String = today(timeZone).toString()

    override fun isoDateToEpoch(dateStr: String?): Long? {
        if (dateStr.isNullOrBlank()) return null
        return runCatching { Instant.parse(dateStr).toEpochMilliseconds() }
            .getOrNull()
    }

    override fun currentYear(timeZone: TimeZone): Int = today(timeZone).year

    override fun formatDisplayDate(date: LocalDate, timeZone: TimeZone): String =
        formatterUtil.formatDateTime(date.atStartOfDayIn(timeZone).toEpochMilliseconds(), DISPLAY_DATE_PATTERN)

    override fun formatDayOfWeek(date: LocalDate, timeZone: TimeZone): String =
        formatterUtil.formatDateTime(date.atStartOfDayIn(timeZone).toEpochMilliseconds(), DAY_OF_WEEK_PATTERN)

    private companion object {
        private const val DISPLAY_DATE_PATTERN = "MMM d, yyyy"
        private const val DAY_OF_WEEK_PATTERN = "EEEE"
    }
}
