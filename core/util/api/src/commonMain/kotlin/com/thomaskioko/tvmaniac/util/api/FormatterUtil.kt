package com.thomaskioko.tvmaniac.util.api

public interface FormatterUtil {
    /** Formats TMDB image url. */
    public fun formatTmdbPosterPath(imageUrl: String): String

    /** Rounds of a double number to the set decimal point. */
    public fun formatDouble(number: Double?, scale: Int): Double

    /** Formats a given number and adds a prefix. e.g 1000 -> 1k */
    public fun formatDuration(number: Int): String

    /**
     * Formats a count as a grouped decimal at or below `9,999` (e.g. `1,250`) and a compact form
     * above it (e.g. `10,800` becomes `10.8K`) so large counts stay narrow in fixed-width tiles.
     * Android localizes the compact suffix; iOS uses a fixed `K`/`M`/`B` suffix.
     */
    public fun formatCompactNumber(number: Long): String

    /** Formats epoch milliseconds to a human-readable date/time string using the given pattern. */
    public fun formatDateTime(epochMillis: Long, pattern: String): String
}
