package com.thomakioko.tvmaniac.util.testing

import com.thomaskioko.tvmaniac.util.FormatterUtil

class FakeFormatterUtil : FormatterUtil {
    override fun formatTmdbPosterPath(imageUrl: String): String = ""

    override fun formatDouble(number: Double?, scale: Int): Double {
        return number ?: 0.0
    }

    override fun formatDuration(number: Int): String = ""
}
