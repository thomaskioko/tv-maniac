package com.thomaskioko.tvmaniac.continuewatching.implementation

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
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingDao
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
public class ContinueWatchingStore(
    private val traktSyncDataSource: TraktSyncRemoteDataSource,
    private val continueWatchingDao: ContinueWatchingDao,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dispatchers: AppCoroutineDispatchers,
) : Store<Unit, List<ContinueWatchingEntry>> by storeBuilder(
    fetcher = apiFetcher { _: Unit ->
        traktSyncDataSource.getWatchedShows()
    },
    sourceOfTruth = SourceOfTruth.of(
        reader = { _: Unit -> continueWatchingDao.entriesObservable() },
        writer = { _: Unit, response: List<TraktWatchedShowResponse> ->
            val incoming = response.map { it.toEntry() }
            val incomingTraktIds = incoming.map { it.traktId }.toSet()
            transactionRunner {
                val existing = continueWatchingDao.entries()
                existing
                    .filter { it.traktId !in incomingTraktIds }
                    .forEach { continueWatchingDao.deleteByTraktId(it.traktId) }
                incoming.forEach { continueWatchingDao.upsert(it) }
            }
            requestManagerRepository.upsert(
                entityId = WATCHED_SHOWS_SYNC.requestId,
                requestType = WATCHED_SHOWS_SYNC.name,
            )
            traktActivityRepository.markActivityAsSynced(ActivityType.EPISODES_WATCHED)
        },
        delete = { _: Unit -> continueWatchingDao.deleteAll() },
        deleteAll = { continueWatchingDao.deleteAll() },
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

// /sync/watched/shows lacks aired/completed counts; placeholder airedEpisodes
// gets overwritten when ProgressPipeline calls /shows/{id}/progress/watched.
private fun TraktWatchedShowResponse.toEntry(): ContinueWatchingEntry = ContinueWatchingEntry(
    traktId = show.ids.trakt,
    tmdbId = show.ids.tmdb,
    airedEpisodes = 0L,
    completedCount = plays,
    lastWatchedAt = Instant.parse(lastWatchedAt).toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse(lastUpdatedAt).toEpochMilliseconds(),
)
