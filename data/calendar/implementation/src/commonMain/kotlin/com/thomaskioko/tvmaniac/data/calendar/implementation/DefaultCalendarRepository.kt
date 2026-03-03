package com.thomaskioko.tvmaniac.data.calendar.implementation

import com.thomaskioko.tvmaniac.data.calendar.CalendarDao
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.CalendarRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultCalendarRepository(
    private val store: CalendarStore,
    private val calendarDao: CalendarDao,
    private val traktAuthRepository: TraktAuthRepository,
) : CalendarRepository {

    override fun observeCalendarEntries(startDate: Long, endDate: Long): Flow<List<CalendarEntry>> =
        calendarDao.observeEntriesBetweenDates(startDate, endDate)

    override suspend fun fetchCalendar(startDate: String, days: Int, forceRefresh: Boolean) {
        if (!traktAuthRepository.isLoggedIn()) return

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
