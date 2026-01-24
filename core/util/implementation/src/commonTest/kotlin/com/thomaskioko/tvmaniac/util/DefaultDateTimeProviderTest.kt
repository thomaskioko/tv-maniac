package com.thomaskioko.tvmaniac.util

import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

internal class DefaultDateTimeProviderTest {

    private val fakeFormatterUtil = FakeFormatterUtil()
    private val underTest = DefaultDateTimeProvider(fakeFormatterUtil)

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
        val result = underTest.epochToIsoDate(epochMillis)

        result shouldBe "2023-12-12"
    }

    @Test
    fun `should return correct date string given epoch milliseconds and timezone`() {
        val epochMillis = 1702386411000L
        val result = underTest.epochToIsoDate(epochMillis, TimeZone.UTC)

        result shouldBe "2023-12-12"
    }

    @Test
    fun `should return correct date string given epoch milliseconds with added days`() {
        val time = LocalDateTime(2023, 12, 12, 9, 0).date.atStartOfDayIn(TimeZone.UTC)
        val instant = time.plus(122.days)
        val result = underTest.epochToIsoDate(instant.toEpochMilliseconds())

        result shouldBe "2024-04-12"
    }

    @Test
    fun `should return year string given valid date string`() {
        val result = underTest.extractYear("2023-12-12")

        result shouldBe "2023"
    }

    @Test
    fun `should return double dash given empty date string`() {
        val result = underTest.extractYear("")

        result shouldBe "--"
    }

    @Test
    fun `should return TBA given invalid date format`() {
        val result = underTest.extractYear("invalid-date")

        result shouldBe "TBA"
    }

    @Test
    fun `should return correct year given various valid date strings`() {
        underTest.extractYear("2024-01-15") shouldBe "2024"
        underTest.extractYear("1999-06-30") shouldBe "1999"
        underTest.extractYear("2030-12-31") shouldBe "2030"
    }

    @Test
    fun `should return year given ISO 8601 datetime string`() {
        underTest.extractYear("2025-11-07T02:00:00.000Z") shouldBe "2025"
        underTest.extractYear("2023-06-15T14:30:00.000Z") shouldBe "2023"
        underTest.extractYear("1999-12-31T23:59:59.999Z") shouldBe "1999"
    }

    @Test
    fun `should return formatted display date time given epoch milliseconds`() {
        fakeFormatterUtil.setFormattedDateTime("Jan 23, 2025 at 11:24")
        val epochMillis = 1737630240000L

        val result = underTest.epochToDisplayDateTime(epochMillis, TimeZone.UTC)

        result shouldBe "Jan 23, 2025 at 11:24"
    }
}
