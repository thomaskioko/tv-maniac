package com.thomaskioko.tvmaniac.base.util

import co.touchlab.kermit.Logger
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject
import java.text.SimpleDateFormat
import java.util.Locale

@Inject
class AndroidDateFormatter : DateFormatter {

    override fun getTimestampMilliseconds(): Long = Clock.System.now().toEpochMilliseconds()

    override fun formatDateString(datePattern: String, dateString: String): String {
        return try {
            val date = SimpleDateFormat("yyyy-mm-dd", Locale.getDefault())
                .parse(dateString)
            SimpleDateFormat(datePattern, Locale.getDefault()).format(date!!)
        } catch (exception: Exception) {
            Logger.e("formatDate::  $dateString ${exception.message}", exception)
            "TBA"
        }
    }
}