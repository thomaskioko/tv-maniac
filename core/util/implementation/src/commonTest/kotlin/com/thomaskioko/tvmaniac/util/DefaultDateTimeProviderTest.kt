package com.thomaskioko.tvmaniac.util

import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

internal class DefaultDateTimeProviderTest {

    private val underTest = DefaultDateTimeProvider()

    @Test
    fun `should return current instant`() {
        val before = Clock.System.now()
        val result = underTest.now()
        val after = Clock.System.now()

        (result >= before) shouldBe true
        (result <= after) shouldBe true
    }

    @Test
    fun `should return current time in milliseconds`() {
        val before = Clock.System.now().toEpochMilliseconds()
        val result = underTest.nowMillis()
        val after = Clock.System.now().toEpochMilliseconds()

        result shouldBeGreaterThan (before - 1)
        (result <= after) shouldBe true
    }

    @Test
    fun `should return current date`() {
        val result = underTest.today()
        val expected = underTest.today()

        result shouldBe expected
    }

    @Test
    fun `should return date in specified timezone given timezone parameter`() {
        val timeZone = TimeZone.UTC
        val result = underTest.today(timeZone)
        val expected = underTest.today(timeZone)

        result shouldBe expected
    }

    @Test
    fun `should return null given null air date`() {
        val result = underTest.calculateDaysUntilAir(null)

        result.shouldBeNull()
    }

    @Test
    fun `should return null given empty air date string`() {
        val result = underTest.calculateDaysUntilAir("")

        result.shouldBeNull()
    }

    @Test
    fun `should return null given blank air date string`() {
        val result = underTest.calculateDaysUntilAir("   ")

        result.shouldBeNull()
    }

    @Test
    fun `should return null given invalid date format`() {
        val result = underTest.calculateDaysUntilAir("invalid-date")

        result.shouldBeNull()
    }

    @Test
    fun `should return null given past date`() {
        val result = underTest.calculateDaysUntilAir("2020-01-01")

        result.shouldBeNull()
    }

    @Test
    fun `should return days until air given future date`() {
        val futureDate = underTest.today().plus(DatePeriod(days = 10))
        val result = underTest.calculateDaysUntilAir(futureDate.toString())

        result shouldBe 10
    }

    @Test
    fun `should return null given past ISO 8601 datetime`() {
        val result = underTest.calculateDaysUntilAir("2020-01-01T04:00:00.000Z")

        result.shouldBeNull()
    }

    @Test
    fun `should return days until air given future ISO 8601 datetime`() {
        val futureDate = underTest.today().plus(DatePeriod(days = 10))
        val result = underTest.calculateDaysUntilAir("${futureDate}T04:00:00.000Z")

        result shouldBe 10
    }

    @Test
    fun `should return midnight instant given timezone`() {
        val timeZone = TimeZone.UTC
        val expected = underTest.now()
            .toLocalDateTime(timeZone)
            .date
            .atStartOfDayIn(timeZone)

        val result = underTest.startOfDay(timeZone)

        result shouldBe expected
    }

    @Test
    fun `should return ISO date string given epoch milliseconds`() {
        val epochMillis = 1702386411000L
        val result = underTest.formatDate(epochMillis)

        result shouldBe "2023-12-12"
    }

    @Test
    fun `should return correct date string given epoch milliseconds and timezone`() {
        val epochMillis = 1702386411000L
        val result = underTest.formatDate(epochMillis, TimeZone.UTC)

        result shouldBe "2023-12-12"
    }

    @Test
    fun `should return correct date string given epoch milliseconds with added days`() {
        val time = LocalDateTime(2023, 12, 12, 9, 0).date.atStartOfDayIn(TimeZone.UTC)
        val instant = time.plus(122.days)
        val result = underTest.formatDate(instant.toEpochMilliseconds())

        result shouldBe "2024-04-12"
    }

    @Test
    fun `should return year string given valid date string`() {
        val result = underTest.getYear("2023-12-12")

        result shouldBe "2023"
    }

    @Test
    fun `should return double dash given empty date string`() {
        val result = underTest.getYear("")

        result shouldBe "--"
    }

    @Test
    fun `should return TBA given invalid date format`() {
        val result = underTest.getYear("invalid-date")

        result shouldBe "TBA"
    }

    @Test
    fun `should return correct year given various valid date strings`() {
        underTest.getYear("2024-01-15") shouldBe "2024"
        underTest.getYear("1999-06-30") shouldBe "1999"
        underTest.getYear("2030-12-31") shouldBe "2030"
    }

    @Test
    fun `should return year given ISO 8601 datetime string`() {
        underTest.getYear("2025-11-07T02:00:00.000Z") shouldBe "2025"
        underTest.getYear("2023-06-15T14:30:00.000Z") shouldBe "2023"
        underTest.getYear("1999-12-31T23:59:59.999Z") shouldBe "1999"
    }
}
