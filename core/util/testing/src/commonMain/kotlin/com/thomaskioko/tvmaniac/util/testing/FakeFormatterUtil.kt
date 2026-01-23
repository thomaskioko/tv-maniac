package com.thomaskioko.tvmaniac.util.testing

import com.thomaskioko.tvmaniac.util.api.FormatterUtil

public class FakeFormatterUtil : FormatterUtil {
    private var formattedDateTime: String = ""

    public fun setFormattedDateTime(value: String) {
        formattedDateTime = value
    }

    override fun formatTmdbPosterPath(imageUrl: String): String = ""

    override fun formatDouble(number: Double?, scale: Int): Double {
        return number ?: 0.0
    }

    override fun formatDuration(number: Int): String = ""

    override fun formatDateTime(epochMillis: Long, pattern: String): String = formattedDateTime
}
