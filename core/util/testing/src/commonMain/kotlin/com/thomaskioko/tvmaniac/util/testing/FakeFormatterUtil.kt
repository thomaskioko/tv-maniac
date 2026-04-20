package com.thomaskioko.tvmaniac.util.testing

import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
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
