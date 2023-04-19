package com.thomaskioko.tvmaniac.util

const val DATE_PATTERN = "EEE, MMM d, yyyy"

interface DateFormatter {
    /**
     * Returns the current time in milliseconds
     *
     * @return timestamp
     */
    fun getTimestampMilliseconds(): Long

    /**
     *  Formats date to provided pattern
     *
     *  @param datePattern
     *  @param dateString
     */
    fun formatDateString(
        datePattern: String = DATE_PATTERN,
        dateString: String,
    ): String
}
