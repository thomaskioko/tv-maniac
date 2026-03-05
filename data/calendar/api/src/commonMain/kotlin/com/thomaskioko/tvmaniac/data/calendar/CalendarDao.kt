package com.thomaskioko.tvmaniac.data.calendar

import kotlinx.coroutines.flow.Flow

public interface CalendarDao {
    public fun observeEntriesBetweenDates(startDate: Long, endDate: Long): Flow<List<CalendarEntry>>
    public fun hasEntriesInRange(startDate: Long, endDate: Long): Boolean
    public fun upsert(entry: CalendarEntry)
    public fun deleteEntriesInRange(startDate: Long, endDate: Long)
    public fun deleteOldEntries(cutoffDate: Long)
    public fun deleteAll()
}
