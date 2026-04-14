package com.thomaskioko.tvmaniac.data.calendar.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.data.calendar.CalendarDao
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.CalendarRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultCalendarRepository(
    private val store: CalendarStore,
    private val calendarDao: CalendarDao,
) : CalendarRepository {

    override fun observeCalendarEntries(startDate: Long, endDate: Long): Flow<List<CalendarEntry>> =
        calendarDao.observeEntriesBetweenDates(startDate, endDate)

    override suspend fun fetchCalendar(startDate: String, days: Int, forceRefresh: Boolean) {
        val params = createParams(startDate, days)
        when {
            forceRefresh -> store.fresh(params)
            else -> store.get(params)
        }
    }

    private fun createParams(startDate: String, days: Int): CalendarParams {
        val startEpoch = parseIsoDate(startDate)
        val endEpoch = startEpoch + (days.toLong() * 24 * 60 * 60 * 1000)
        return CalendarParams(
            startDate = startDate,
            days = days,
            startEpoch = startEpoch,
            endEpoch = endEpoch,
        )
    }

    private fun parseIsoDate(date: String): Long {
        return Instant.parse("${date}T00:00:00Z").toEpochMilliseconds()
    }
}
