package com.thomaskioko.tvmaniac.util

import io.kotest.matchers.equals.shouldBeEqual
import kotlin.test.Test

class IosFormatterUtilTest {

    private val formatterUtil = IosFormatterUtil()

    @Test
    fun `should return formatted poster path`() {
        val imageUrl = "/path/to/image.jpg"
        val expected = "https://image.tmdb.org/t/p/original/path/to/image.jpg"

        val result = formatterUtil.formatTmdbPosterPath(imageUrl)

        result shouldBeEqual expected
    }

    @Test
    fun `should round double to two decimal places`() {
        val number = 3.14159
        val scale = 2
        val expected = 3.15 // Rounds up due to RoundingMode.UP

        val result = formatterUtil.formatDouble(number, scale)

        result shouldBeEqual expected
    }

    @Test
    fun `should round double to one decimal places`() {
        val number = 3.14159
        val expected = 3.2 // Rounds up due to RoundingMode.UP

        val result = formatterUtil.formatDouble(number, 1)

        result shouldBeEqual expected
    }

    @Test
    fun formatDouble_withNull_returnsZero() {
        val number: Double? = null
        val scale = 2
        val expected = 0.0

        val result = formatterUtil.formatDouble(number, scale)

        result shouldBeEqual expected
    }

    @Test
    fun formatDuration_lessThanThousand_returnsPlainNumber() {
        val number = 999
        val expected = "999"

        val result = formatterUtil.formatDuration(number)

        result shouldBeEqual expected
    }

    @Test
    fun formatDuration_thousands_returnsKSuffix() {
        val number = 1500
        val expected = "1.5k"

        val result = formatterUtil.formatDuration(number)

        result shouldBeEqual expected
    }

    @Test
    fun formatDuration_millions_returnsMSuffix() {
        val number = 1500000
        val expected = "1.5M"

        val result = formatterUtil.formatDuration(number)

        result shouldBeEqual expected
    }
}
