package com.thomaskioko.tvmaniac.util

import co.touchlab.kermit.Logger
import dev.zacsweers.metro.Inject
import kotlinx.datetime.LocalDate
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import kotlin.time.Clock

@Inject
actual class PlatformDateFormatter {

    actual fun getTimestampMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()

    actual fun formatDate(epochMillis: Long): String {
        val date = NSDate.dateWithTimeIntervalSince1970(epochMillis / 1000.0)
        val dateFormatter = NSDateFormatter().apply {
            dateFormat = DATE_YYYY_MM_DD_PATTERN
            locale = NSLocale(localeIdentifier = "UTC")
        }
        return dateFormatter.stringFromDate(date)
    }

    actual fun getYear(dateString: String): String {
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
