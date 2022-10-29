package com.thomaskioko.tvmaniac.core.util

import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import java.text.SimpleDateFormat
import java.util.Locale

actual object DateUtil {

    actual fun formatDateString(
        datePattern: String,
        dateString: String?
    ): String {
        var result = "TBA"
        try {
            dateString?.let {
                val date = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
                    .parse(dateString)
                result = SimpleDateFormat(datePattern, Locale.getDefault())
                    .format(date!!)
            }
        } catch (exception: Exception) {
            Logger.e("formatDate::  $dateString ${exception.message}", exception)
        }
        return result
    }

    actual fun getTimestampMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()
}
