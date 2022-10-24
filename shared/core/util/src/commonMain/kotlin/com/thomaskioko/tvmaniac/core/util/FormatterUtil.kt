package com.thomaskioko.tvmaniac.core.util

expect object FormatterUtil {
    fun formatPosterPath(imageUrl: String?): String

    fun formatDouble(number: Double?, scale: Int): Double
}
