package com.thomaskioko.tvmaniac.util.api

public interface FormatterUtil {
    /** Formats TMDB image url. */
    public fun formatTmdbPosterPath(imageUrl: String): String

    /** Rounds of a double number to the set decimal point. */
    public fun formatDouble(number: Double?, scale: Int): Double

    /** Formats a given number and adds a prefix. e.g 1000 -> 1k */
    public fun formatDuration(number: Int): String

    /** Formats epoch milliseconds to a human-readable date/time string using the given pattern. */
    public fun formatDateTime(epochMillis: Long, pattern: String): String
}
