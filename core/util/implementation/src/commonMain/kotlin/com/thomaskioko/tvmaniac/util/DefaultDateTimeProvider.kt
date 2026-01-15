package com.thomaskioko.tvmaniac.util

import co.touchlab.kermit.Logger
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.daysUntil
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

    override fun today(timeZone: TimeZone): LocalDate = Clock.System.todayIn(timeZone)

    override fun calculateDaysUntilAir(
        airDateStr: String?,
        timeZone: TimeZone,
    ): Int? {
        if (airDateStr.isNullOrBlank()) return null
        val airDate = runCatching { LocalDate.parse(airDateStr) }
            .recoverCatching { Instant.parse(airDateStr).toLocalDateTime(timeZone).date }
            .getOrNull() ?: return null
        val days = today(timeZone).daysUntil(airDate)
        return if (days > 0) days else null
    }

    override fun startOfDay(timeZone: TimeZone): Instant = now()
        .toLocalDateTime(timeZone)
        .date
        .atStartOfDayIn(timeZone)

    override fun formatDate(epochMillis: Long, timeZone: TimeZone): String {
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val localDate = instant.toLocalDateTime(timeZone).date
        return localDate.toString()
    }

    override fun formatDateTime(epochMillis: Long, timeZone: TimeZone): String {
        val instant = Instant.fromEpochMilliseconds(epochMillis)
        val localDateTime = instant.toLocalDateTime(timeZone)
        val date = localDateTime.date
        val hour = localDateTime.hour.toString().padStart(2, '0')
        val minute = localDateTime.minute.toString().padStart(2, '0')
        return "$date $hour:$minute"
    }

    override fun getYear(dateString: String): String {
        if (dateString.isEmpty()) return "--"
        return runCatching { LocalDate.parse(dateString).year }
            .recoverCatching { Instant.parse(dateString).toLocalDateTime(TimeZone.UTC).year }
            .map { it.toString() }
            .getOrElse { exception ->
                Logger.e("getYear:: $dateString ${exception.message}", exception)
                "TBA"
            }
    }
}
