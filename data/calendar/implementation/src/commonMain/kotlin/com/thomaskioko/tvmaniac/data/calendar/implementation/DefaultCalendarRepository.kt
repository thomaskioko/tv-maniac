package com.thomaskioko.tvmaniac.data.calendar.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.data.calendar.CalendarDao
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.CalendarRepository
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Instant

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultCalendarRepository(
    private val store: CalendarStore,
    private val calendarDao: CalendarDao,
    private val followedShowsDao: FollowedShowsDao,
    private val accountManager: AccountManager,
    private val syncObserver: SyncObserver,
) : CalendarRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeCalendarEntries(startDate: Long, endDate: Long): Flow<List<CalendarEntry>> =
        followedShowsDao.entriesObservable()
            .map { entries -> entries.mapNotNull { it.tmdbId }.toSet() }
            .distinctUntilChanged()
            .flatMapLatest {
                channelFlow {
                    launch { refreshIfPossible(startDate, endDate) }
                    calendarDao.observeEntriesBetweenDates(startDate, endDate)
                        .collect { send(it) }
                }
            }

    override suspend fun fetchCalendar(startDate: String, days: Int, forceRefresh: Boolean) {
        val params = createParams(startDate, days)
        when {
            forceRefresh -> store.fresh(params)
            else -> store.get(params)
        }
    }

    private suspend fun refreshIfPossible(startEpoch: Long, endEpoch: Long) {
        if (accountManager.getActiveProvider() == null) return
        try {
            store.get(createParams(startEpoch, endEpoch))
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            syncObserver.log(SyncError.BackgroundSyncFailed(operationId = TAG, cause = throwable))
        }
    }

    private fun createParams(startDate: String, days: Int): CalendarParams {
        val startEpoch = parseIsoDate(startDate)
        val endEpoch = startEpoch + (days.toLong() * DAY_MILLIS)
        return CalendarParams(
            startDate = startDate,
            days = days,
            startEpoch = startEpoch,
            endEpoch = endEpoch,
        )
    }

    private fun createParams(startEpoch: Long, endEpoch: Long): CalendarParams {
        val days = ((endEpoch - startEpoch) / DAY_MILLIS).toInt()
        val startDate = Instant.fromEpochMilliseconds(startEpoch).toString().substringBefore('T')
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

    private companion object {
        private const val TAG = "Calendar"
        private const val DAY_MILLIS = 24L * 60 * 60 * 1000
    }
}
