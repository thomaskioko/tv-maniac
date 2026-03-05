package com.thomaskioko.tvmaniac.domain.calendar

import com.thomaskioko.tvmaniac.domain.calendar.model.DateLabel
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test

internal class CalendarWeekCalculatorTest {

    private val dateTimeProvider = FakeDateTimeProvider()
    private val formatterUtil = FakeFormatterUtil()

    private fun createCalculator(): CalendarWeekCalculator =
        CalendarWeekCalculator(
            dateTimeProvider = dateTimeProvider,
            formatterUtil = formatterUtil,
        )

    @Test
    fun `should return today to today plus 7 days given week offset is zero`() {
        val calculator = createCalculator()
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val expectedEnd = today.plus(CalendarWeekCalculator.DAYS_IN_WEEK, DateTimeUnit.DAY)

        val (start, end) = calculator.getWeekRange(0)

        start shouldBe today
        end shouldBe expectedEnd
    }

    @Test
    fun `should return offset week range given positive week offset`() {
        val calculator = createCalculator()
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val expectedStart = today.plus(14, DateTimeUnit.DAY)
        val expectedEnd = expectedStart.plus(CalendarWeekCalculator.DAYS_IN_WEEK, DateTimeUnit.DAY)

        val (start, end) = calculator.getWeekRange(2)

        start shouldBe expectedStart
        end shouldBe expectedEnd
    }

    @Test
    fun `should return epoch millis for week range given week offset`() {
        val calculator = createCalculator()
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val expectedStartEpoch = today.atStartOfDayIn(timeZone).toEpochMilliseconds()
        val expectedEndDate = today.plus(CalendarWeekCalculator.DAYS_IN_WEEK, DateTimeUnit.DAY)
        val expectedEndEpoch = expectedEndDate.atStartOfDayIn(timeZone).toEpochMilliseconds()

        val (startEpoch, endEpoch) = calculator.getWeekEpochRange(0)

        startEpoch shouldBe expectedStartEpoch
        endEpoch shouldBe expectedEndEpoch
    }

    @Test
    fun `should return iso date string given week offset`() {
        val calculator = createCalculator()
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date

        val result = calculator.getStartDateForOffset(0)

        result shouldBe today.toString()
    }

    @Test
    fun `should return offset iso date string given positive week offset`() {
        val calculator = createCalculator()
        val timeZone = TimeZone.currentSystemDefault()
        val today = dateTimeProvider.now().toLocalDateTime(timeZone).date
        val expectedStart = today.plus(7, DateTimeUnit.DAY)

        val result = calculator.getStartDateForOffset(1)

        result shouldBe expectedStart.toString()
    }

    @Test
    fun `should return formatted week label given week offset`() {
        formatterUtil.setFormattedDateTime("Jan 1, 2024")
        val calculator = createCalculator()

        val result = calculator.formatWeekLabel(0)

        result shouldBe "Jan 1, 2024 - Jan 1, 2024"
    }

    @Test
    fun `should return today label given date equals today`() {
        formatterUtil.setFormattedDateTime("Jan 1, 2024")
        val calculator = createCalculator()
        val today = LocalDate(2024, 1, 1)
        val tomorrow = LocalDate(2024, 1, 2)

        val result = calculator.formatDateLabel(today, today, tomorrow)

        result.shouldBeInstanceOf<DateLabel.Today>()
        result.formattedDate shouldBe "Jan 1, 2024"
    }

    @Test
    fun `should return tomorrow label given date equals tomorrow`() {
        formatterUtil.setFormattedDateTime("Jan 2, 2024")
        val calculator = createCalculator()
        val today = LocalDate(2024, 1, 1)
        val tomorrow = LocalDate(2024, 1, 2)

        val result = calculator.formatDateLabel(tomorrow, today, tomorrow)

        result.shouldBeInstanceOf<DateLabel.Tomorrow>()
        result.formattedDate shouldBe "Jan 2, 2024"
    }

    @Test
    fun `should return day name label given date is neither today nor tomorrow`() {
        formatterUtil.setFormattedDateTime("Wednesday")
        val calculator = createCalculator()
        val today = LocalDate(2024, 1, 1)
        val tomorrow = LocalDate(2024, 1, 2)
        val otherDate = LocalDate(2024, 1, 3)

        val result = calculator.formatDateLabel(otherDate, today, tomorrow)

        val dayOfWeek = result.shouldBeInstanceOf<DateLabel.DayOfWeek>()
        dayOfWeek.dayName shouldBe "Wednesday"
        dayOfWeek.formattedDate shouldBe "Wednesday"
    }
}
