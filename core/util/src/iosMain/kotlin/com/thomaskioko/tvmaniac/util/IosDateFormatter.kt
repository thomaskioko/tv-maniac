package com.thomaskioko.tvmaniac.util

import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSISO8601DateFormatter

@Inject
class IosDateFormatter : DateFormatter {

    override fun getTimestampMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()

    override fun formatDateString(datePattern: String, dateString: String): String {
        var result = "TBA"
        try {
            val date = getDateFromIso8601Timestamp(dateString) ?: return ""

            val dateFormatter = NSDateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd"

            result = dateFormatter.stringFromDate(date)
        } catch (exception: Exception) {
            Logger.e("formatDate::  $dateString ${exception.message}", exception)
        }
        return result
    }

    private fun getDateFromIso8601Timestamp(string: String): NSDate? {
        return NSISO8601DateFormatter().dateFromString(string)
    }
}
