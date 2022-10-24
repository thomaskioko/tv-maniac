package com.thomaskioko.tvmaniac.core.util

const val DATE_PATTERN = "EEE, MMM d, yyyy"

expect object DateUtil {

    /**
     *  Formats date to provided pattern
     *
     *  @param datePattern
     *  @param dateString
     */
    fun formatDateString(
        datePattern: String = DATE_PATTERN,
        dateString: String?
    ): String

    fun getTimestampMilliseconds(): Long
}
