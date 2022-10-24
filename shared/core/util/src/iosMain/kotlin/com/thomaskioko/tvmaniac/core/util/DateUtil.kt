package com.thomaskioko.tvmaniac.core.util

import kotlinx.datetime.Clock
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual object DateUtil {

    actual fun formatDateString(
        datePattern: String,
        dateString: String?
    ): String {
        val dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "yyyy-mm-dd"
        dateFormatter.locale = NSLocale.currentLocale()

        val newDateFormatter = NSDateFormatter()
        newDateFormatter.dateFormat = datePattern
        newDateFormatter.locale = NSLocale.currentLocale()

        var date = "TBA"
        dateString?.let {
            val formattedDate = dateFormatter.dateFromString(it)
            date = newDateFormatter.stringFromDate(formattedDate!!)
        }

        return date
    }

    actual fun getTimestampMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()
}
