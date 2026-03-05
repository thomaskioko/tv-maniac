package com.thomaskioko.tvmaniac.data.calendar.testing

import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.CalendarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

public class FakeCalendarRepository : CalendarRepository {

    private val entriesFlow = MutableStateFlow<List<CalendarEntry>>(emptyList())

    public fun setCalendarEntries(entries: List<CalendarEntry>) {
        entriesFlow.value = entries
    }

    public fun setFetchError(error: Throwable?) {
    }

    override fun observeCalendarEntries(startDate: Long, endDate: Long): Flow<List<CalendarEntry>> =
        entriesFlow.asStateFlow()

    override suspend fun fetchCalendar(startDate: String, days: Int, forceRefresh: Boolean) {
    }
}
