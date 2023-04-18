package com.thomaskioko.tvmaniac.util

interface FormatterUtil {
    /**
     * Formats TMDB image url. If the url is null, we return a default imageUrl
     */
    fun formatTmdbPosterPath(imageUrl: String?): String

    /**
     * Rounds of a double number to the set decimal point.
     */
    fun formatDouble(number: Double?, scale: Int): Double

    /**
     * Formats a given number and adds a prefix. e.g 1000 -> 1k
     */
    fun formatDuration(number: Int): String
}
