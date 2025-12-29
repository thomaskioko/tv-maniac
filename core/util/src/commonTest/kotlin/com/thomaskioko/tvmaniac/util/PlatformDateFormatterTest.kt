package com.thomaskioko.tvmaniac.util

import io.kotest.matchers.equals.shouldBeEqual
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.Test
import kotlin.time.Duration.Companion.days

internal class PlatformDateFormatterTest {

    private val underTest = PlatformDateFormatter()

    @Test
    fun `test format timeStamp`() {
        val actual = underTest.formatDate(1702386411000)

        actual shouldBeEqual "2023-12-12"
    }

    @Test
    fun `given a date verify year is returned`() {
        val actual = underTest.getYear("2023-12-12")

        actual shouldBeEqual "2023"
    }

    @Test
    fun `test format timeStamp plus months`() {
        val time = LocalDateTime(2023, 12, 12, 9, 0).date.atStartOfDayIn(TimeZone.UTC)
        val instant = time.plus(122.days).toEpochMilliseconds()
        val actual = underTest.formatDate(instant)

        actual shouldBeEqual "2024-04-12"
    }
}
