package com.thomaskioko.tvmaniac.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject
import kotlin.time.Clock

/**
 * Provides date and time utilities for the application.
 * This class contains common date operations that don't require platform-specific implementations.
 */
@Inject
class DateTimeProvider {

    /**
     * Returns today's date in YYYY-MM-DD format
     */
    fun getTodayDate(): String {
        val now = Clock.System.now()
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return today.toString()
    }

    /**
     * Returns a date in the past in YYYY-MM-DD format
     *
     * @param monthsAgo number of months to subtract from today
     */
    fun getDateMonthsAgo(monthsAgo: Int): String {
        val now = Clock.System.now()
        val pastDate = now.minus(monthsAgo, DateTimeUnit.MONTH, TimeZone.currentSystemDefault())
        val date = pastDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return date.toString()
    }
}
