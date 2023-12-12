package com.thomaskioko.tvmaniac.util

import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.dateWithTimeIntervalSince1970

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
}
