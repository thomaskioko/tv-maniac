package com.thomaskioko.tvmaniac.data.calendar.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.data.calendar.CalendarDao
import com.thomaskioko.tvmaniac.data.calendar.CalendarEntry
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.CALENDAR_SHOWS
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.trakt.api.TraktCalendarRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarResponse
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator
import kotlin.time.Instant

public data class CalendarParams(
    val startDate: String,
    val days: Int,
    val startEpoch: Long,
    val endEpoch: Long,
)

@Inject
public class CalendarStore(
    private val calendarDataSource: TraktCalendarRemoteDataSource,
    private val calendarDao: CalendarDao,
    private val tvShowsDao: TvShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val databaseTransactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<CalendarParams, List<CalendarEntry>> by storeBuilder(
    fetcher = apiFetcher { params: CalendarParams ->
        calendarDataSource.getMyShowsCalendar(params.startDate, params.days)
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { params: CalendarParams ->
            calendarDao.observeEntriesBetweenDates(params.startEpoch, params.endEpoch)
        },
        writer = { params: CalendarParams, response: List<TraktCalendarResponse> ->
            withContext(dispatchers.databaseWrite) {
                databaseTransactionRunner {
                    calendarDao.deleteEntriesInRange(params.startEpoch, params.endEpoch)

                    val showTraktIds = response.map { it.show.ids.trakt.toLong() }.distinct()
                    val showPosters = tvShowsDao.getShowsByTraktIds(showTraktIds)
                        .associate { it.traktId to it.posterPath }

                    response.forEach { calendarResponse ->
                        val showTraktId = calendarResponse.show.ids.trakt
                        val firstAiredEpoch = Instant.parse(calendarResponse.firstAired).toEpochMilliseconds()

                        val posterPath = showPosters[showTraktId]

                        calendarDao.upsert(
                            CalendarEntry(
                                showTraktId = showTraktId,
                                episodeTraktId = calendarResponse.episode.ids.trakt.toLong(),
                                seasonNumber = calendarResponse.episode.seasonNumber,
                                episodeNumber = calendarResponse.episode.episodeNumber,
                                episodeTitle = calendarResponse.episode.title,
                                airDate = firstAiredEpoch,
                                showTitle = calendarResponse.show.title,
                                showPosterPath = posterPath,
                                network = null,
                                runtime = calendarResponse.episode.runtime,
                                overview = calendarResponse.episode.overview,
                                rating = calendarResponse.episode.rating,
                                votes = calendarResponse.episode.votes,
                            ),
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
    Validator.by {
        withContext(dispatchers.io) {
            requestManagerRepository.isRequestValid(
                requestType = CALENDAR_SHOWS.name,
                threshold = CALENDAR_SHOWS.duration,
            )
        }
    },
).build()
