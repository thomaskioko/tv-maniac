package com.thomaskioko.tvmaniac.domain.calendar

import com.thomaskioko.tvmaniac.domain.calendar.model.DateLabel
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.Inject
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

@Inject
public class CalendarWeekCalculator(
    private val dateTimeProvider: DateTimeProvider,
) {

    private val timeZone: TimeZone = dateTimeProvider.getTimeZone()

    public fun getWeekRange(weekOffset: Int): Pair<LocalDate, LocalDate> {
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val startDate = today.plus(weekOffset * DAYS_IN_WEEK, DateTimeUnit.DAY)
        val endDate = startDate.plus(DAYS_IN_WEEK, DateTimeUnit.DAY)
        return startDate to endDate
    }

    public fun getWeekEpochRange(weekOffset: Int): Pair<Long, Long> {
        val (startDate, endDate) = getWeekRange(weekOffset)
        val startEpoch = startDate.atStartOfDayIn(timeZone)
        val endEpoch = endDate.atStartOfDayIn(timeZone)
        return startEpoch.toEpochMilliseconds() to endEpoch.toEpochMilliseconds()
    }

    public fun getStartDateForOffset(weekOffset: Int): String {
        val (startDate, _) = getWeekRange(weekOffset)
        return startDate.toString()
    }

    public fun formatWeekLabel(weekOffset: Int): String {
        val (startDate, endDate) = getWeekRange(weekOffset)
        val inclusiveEndDate = endDate.plus(-1, DateTimeUnit.DAY)
        val startFormatted = dateTimeProvider.formatDisplayDate(startDate)
        val endFormatted = dateTimeProvider.formatDisplayDate(inclusiveEndDate)
        return "$startFormatted - $endFormatted"
    }

    public fun formatDateLabel(date: LocalDate, today: LocalDate, tomorrow: LocalDate): DateLabel {
        val formattedDate = dateTimeProvider.formatDisplayDate(date)
        return when (date) {
            today -> DateLabel.Today(formattedDate = formattedDate)
            tomorrow -> DateLabel.Tomorrow(formattedDate = formattedDate)
            else -> DateLabel.DayOfWeek(
                dayName = dateTimeProvider.formatDayOfWeek(date),
                formattedDate = formattedDate,
            )
        }
    }

    public companion object {
        public const val DAYS_IN_WEEK: Int = 7
    }
}
