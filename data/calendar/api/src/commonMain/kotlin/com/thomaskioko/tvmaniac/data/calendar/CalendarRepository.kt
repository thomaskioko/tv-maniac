package com.thomaskioko.tvmaniac.data.calendar

import kotlinx.coroutines.flow.Flow

public interface CalendarRepository {
    public fun observeCalendarEntries(startDate: Long, endDate: Long): Flow<List<CalendarEntry>>
    public suspend fun fetchCalendar(startDate: String, days: Int, forceRefresh: Boolean)
}
