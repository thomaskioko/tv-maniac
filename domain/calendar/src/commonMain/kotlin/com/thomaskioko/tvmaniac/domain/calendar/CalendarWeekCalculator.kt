package com.thomaskioko.tvmaniac.domain.calendar

import com.thomaskioko.tvmaniac.domain.calendar.model.DateLabel
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject

@Inject
public class CalendarWeekCalculator(
    private val dateTimeProvider: DateTimeProvider,
    private val formatterUtil: FormatterUtil,
) {

    private val timeZone: TimeZone = TimeZone.currentSystemDefault()

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
        val adjustedEndDate = endDate.plus(-1, DateTimeUnit.DAY)
        val startEpoch = startDate.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val endEpoch = adjustedEndDate.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val startFormatted = formatterUtil.formatDateTime(startEpoch, "MMM d, yyyy")
        val endFormatted = formatterUtil.formatDateTime(endEpoch, "MMM d, yyyy")
        return "$startFormatted - $endFormatted"
    }

    public fun formatDateLabel(date: LocalDate, today: LocalDate, tomorrow: LocalDate): DateLabel {
        val dateEpoch = date.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val dateFormatted = formatterUtil.formatDateTime(dateEpoch, "MMM d, yyyy")

        return when (date) {
            today -> DateLabel.Today(formattedDate = dateFormatted)
            tomorrow -> DateLabel.Tomorrow(formattedDate = dateFormatted)
            else -> {
                val dayName = formatterUtil.formatDateTime(dateEpoch, "EEEE")
                DateLabel.DayOfWeek(dayName = dayName, formattedDate = dateFormatted)
            }
        }
    }

    public companion object {
        public const val DAYS_IN_WEEK: Int = 7
    }
}
