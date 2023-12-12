package com.thomaskioko.tvmaniac.util

import kotlinx.datetime.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

actual class PlatformDateFormatter {

    actual fun getTimestampMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()

    actual fun formatDate(epochMillis: Long): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        val localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern(DATE_YYYY_MM_DD_PATTERN).withZone(ZoneId.of("UTC"))
        return localDate.format(formatter)
    }
}
