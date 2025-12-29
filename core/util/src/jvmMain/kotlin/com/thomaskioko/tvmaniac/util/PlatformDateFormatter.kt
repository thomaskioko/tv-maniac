package com.thomaskioko.tvmaniac.util

import co.touchlab.kermit.Logger
import kotlinx.datetime.LocalDate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Clock

public actual class PlatformDateFormatter {

    public actual fun getTimestampMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()

    public actual fun formatDate(epochMillis: Long): String {
        val instant = Instant.ofEpochMilli(epochMillis)
        val localDate = instant.atZone(ZoneId.of("UTC")).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern(DATE_YYYY_MM_DD_PATTERN).withZone(ZoneId.of("UTC"))
        return localDate.format(formatter)
    }

    public actual fun getYear(dateString: String): String {
        if (dateString.isEmpty()) return "--"
        return try {
            val localDate = LocalDate.parse(dateString)
            localDate.year.toString()
        } catch (exception: Exception) {
            Logger.e("formatDate::  $dateString ${exception.message}", exception)
            "TBA"
        }
    }
}
