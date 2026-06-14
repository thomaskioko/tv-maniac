package com.thomaskioko.tvmaniac.data.calendar.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.AuthenticationException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.getOrThrow
import com.thomaskioko.tvmaniac.data.calendar.CalendarDao
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.CalendarRemoteDataSource
import com.thomaskioko.tvmaniac.data.calendar.RemoteCalendarEntry
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.CALENDAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator

public data class CalendarParams(
    val startDate: String,
    val days: Int,
    val startEpoch: Long,
    val endEpoch: Long,
)

@Inject
public class CalendarStore(
    private val activeSource: () -> CalendarRemoteDataSource?,
    private val calendarDao: CalendarDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<CalendarParams, List<CalendarEntry>> by storeBuilder(
    fetcher = Fetcher.of { params: CalendarParams ->
        val source = activeSource() ?: throw AuthenticationException("No active calendar provider")
        source.getCalendarEntries(params.startDate, params.days).getOrThrow()
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { params: CalendarParams ->
            calendarDao.observeEntriesBetweenDates(params.startEpoch, params.endEpoch)
        },
        writer = { params: CalendarParams, response: List<RemoteCalendarEntry> ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    calendarDao.deleteEntriesInRange(params.startEpoch, params.endEpoch)

                    val tmdbIds = response.map { it.tmdbId }.distinct()
                    val postersByTmdbId = tvShowsDao.getShowsByIds(tmdbIds)
                        .associate { it.tmdbId to it.posterPath }

                    response.forEach { entry ->
                        calendarDao.upsertFromRemote(
                            entry = entry,
                            posterPath = postersByTmdbId[entry.tmdbId],
                        )
                    }
                }
            }

            requestManagerRepository.upsert(
                entityId = CALENDAR_SHOWS.requestId,
                requestType = CALENDAR_SHOWS.name,
            )
        },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by { cachedData ->
        withContext(dispatchers.io) {
            cachedData.isNotEmpty() && requestManagerRepository.isRequestValid(
                requestType = CALENDAR_SHOWS.name,
                threshold = CALENDAR_SHOWS.duration,
            )
        }
    },
).build()
