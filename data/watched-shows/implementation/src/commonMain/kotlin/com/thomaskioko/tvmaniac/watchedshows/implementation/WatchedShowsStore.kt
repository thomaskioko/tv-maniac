package com.thomaskioko.tvmaniac.watchedshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.apiFetcher
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.storeBuilder
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.usingDispatchers
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCHED_SHOWS_SYNC
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.trakt.api.TraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowEntry
import com.thomaskioko.tvmaniac.watchedshows.api.WatchedShowsDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.Validator
import kotlin.time.Instant

@Inject
@SingleIn(AppScope::class)
public class WatchedShowsStore(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val watchedShowsDao: WatchedShowsDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Unit, List<WatchedShowEntry>> by storeBuilder(
    fetcher = apiFetcher { _: Unit ->
        traktSyncDataSource.getWatchedShows()
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: Unit -> watchedShowsDao.entriesObservable() },
        writer = { _: Unit, response: List<TraktWatchedShowResponse> ->
            val incoming = response.map { it.toEntry() }
            val incomingTraktIds = incoming.map { it.traktId }.toSet()
            transactionRunner {
                val existing = watchedShowsDao.entries()
                existing
                    .filter { it.traktId !in incomingTraktIds }
                    .forEach { watchedShowsDao.deleteByTraktId(it.traktId) }
                incoming.forEach { watchedShowsDao.upsert(it) }
            }
            requestManagerRepository.upsert(
                entityId = WATCHED_SHOWS_SYNC.requestId,
                requestType = WATCHED_SHOWS_SYNC.name,
            )
            traktActivityRepository.markActivityAsSynced(ActivityType.EPISODES_WATCHED)
        },
        delete = { _: Unit -> watchedShowsDao.deleteAll() },
        deleteAll = { watchedShowsDao.deleteAll() },
    ).usingDispatchers(
        readDispatcher = dispatchers.databaseRead,
        writeDispatcher = dispatchers.databaseWrite,
    ),
).validator(
    Validator.by {
        withContext(dispatchers.io) {
            val ttlValid = requestManagerRepository.isRequestValid(
                requestType = WATCHED_SHOWS_SYNC.name,
                threshold = WATCHED_SHOWS_SYNC.duration,
            )
            val activityChanged = traktActivityRepository.hasActivityChanged(ActivityType.EPISODES_WATCHED)
            ttlValid && !activityChanged
        }
    },
).build()

private fun TraktWatchedShowResponse.toEntry(): WatchedShowEntry = WatchedShowEntry(
    traktId = show.ids.trakt,
    tmdbId = show.ids.tmdb,
    plays = plays,
    lastWatchedAt = Instant.parse(lastWatchedAt).toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse(lastUpdatedAt).toEpochMilliseconds(),
)
