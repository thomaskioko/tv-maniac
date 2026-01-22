package com.thomaskioko.tvmaniac.util

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Clock
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultDateTimeProvider : DateTimeProvider {
    override fun now(): Instant = Clock.System.now()

    private fun today(timeZone: TimeZone): LocalDate = Clock.System.todayIn(timeZone)

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
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val localDateTime = instant.toLocalDateTime(timeZone)
        val date = localDateTime.date
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        return "$date $hour:$minute"
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
}
