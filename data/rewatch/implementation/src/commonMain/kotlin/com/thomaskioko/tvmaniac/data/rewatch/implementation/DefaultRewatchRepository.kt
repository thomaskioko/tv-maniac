package com.thomaskioko.tvmaniac.data.rewatch.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchClose
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchSessionStatus
import com.thomaskioko.tvmaniac.data.rewatch.api.RemoteRewatchWrite
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchRepository
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSession
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSessionDao
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchStatus
import com.thomaskioko.tvmaniac.data.rewatch.api.RewatchSyncProviderDataSource
import com.thomaskioko.tvmaniac.shows.api.TvShowsDao
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultRewatchRepository(
    private val rewatchSessionDao: RewatchSessionDao,
    private val tvShowsDao: TvShowsDao,
    private val activeSource: () -> RewatchSyncProviderDataSource?,
    private val syncObserver: SyncObserver,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
) : RewatchRepository {

    private val syncMutex = Mutex()

    override suspend fun startSession(showId: Long, startedAt: Long): Long? {
        val localShowId = tvShowsDao.getLocalShowIdByTmdbId(showId) ?: return null
        return rewatchSessionDao.openSession(showId = localShowId, startedAt = startedAt)
    }

    override suspend fun addEpisodeToSession(
        sessionId: Long,
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
    ) {
        syncMutex.withLock {
            val rowId = rewatchSessionDao.addEpisodeToSession(sessionId = sessionId, episodeId = episodeId, watchedAt = watchedAt)
            pushRewatch(
                rowId = rowId,
                localSessionId = sessionId,
                providerShowId = showId,
                seasonNumber = seasonNumber,
                episodeNumber = episodeNumber,
                watchedAt = watchedAt,
            )
            closeIfShowCovered(sessionId = sessionId, closedAt = watchedAt)
        }
    }

    override suspend fun closeSession(sessionId: Long, closedAt: Long) {
        rewatchSessionDao.closeSession(sessionId = sessionId, closedAt = closedAt)
    }

    override suspend fun finishSession(sessionId: Long, closedAt: Long) {
        val session = rewatchSessionDao.sessionById(sessionId)
        rewatchSessionDao.closeSession(sessionId = sessionId, closedAt = closedAt)

        val providerSessionId = session?.providerSessionId ?: return
        val providerShowId = tvShowsDao.getTmdbIdForLocalShowId(session.showId) ?: return

        syncMutex.withLock {
            val source = activeSource() ?: return@withLock
            try {
                val response = source.closeRewatch(
                    RemoteRewatchClose(providerShowId = providerShowId, providerSessionId = providerSessionId),
                )
                if (response is ApiResponse.Error) {
                    syncObserver.log(SyncError.BackgroundSyncFailed(operationId = TAG, cause = response.toThrowable()))
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                syncObserver.log(SyncError.BackgroundSyncFailed(operationId = TAG, cause = throwable))
            }
        }
    }

    override fun observeSessionsForShow(showId: Long): Flow<List<RewatchSession>> = flow {
        val localShowId = tvShowsDao.getLocalShowIdByTmdbId(showId)
        if (localShowId == null) {
            emit(emptyList())
        } else {
            emitAll(rewatchSessionDao.observeSessionsForShow(localShowId))
        }
    }.flowOn(dispatchers.databaseRead)

    override fun observeRewatchStatus(showId: Long): Flow<RewatchStatus> = flow {
        val localShowId = tvShowsDao.getLocalShowIdByTmdbId(showId)
        if (localShowId == null) {
            emit(RewatchStatus())
        } else {
            emitAll(rewatchSessionDao.observeRewatchStatus(localShowId))
        }
    }.flowOn(dispatchers.databaseRead)

    override suspend fun openSessionForShow(showId: Long): RewatchSession? {
        val localShowId = tvShowsDao.getLocalShowIdByTmdbId(showId) ?: return null
        return rewatchSessionDao.openSessionForShow(localShowId)
    }

    override fun playCountForEpisode(episodeId: Long): Long = rewatchSessionDao.playCountForEpisode(episodeId)

    override suspend fun supportsRewatch(): Boolean = activeSource()?.supportsRewatch() ?: true

    override suspend fun syncPendingRewatches() {
        syncMutex.withLock { drainUnsentEpisodes() }
    }

    override suspend fun syncRewatchSessions(showId: Long): List<RemoteRewatchSession> = syncMutex.withLock {
        val source = activeSource() ?: return@withLock emptyList()
        try {
            when (val response = source.readRewatchSessions(showId)) {
                is ApiResponse.Success -> response.body.also { backfillSessions(showId = showId, sessions = it) }
                is ApiResponse.Unauthenticated -> emptyList()
                is ApiResponse.Error -> {
                    syncObserver.log(SyncError.BackgroundSyncFailed(operationId = TAG, cause = response.toThrowable()))
                    emptyList()
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            syncObserver.log(SyncError.BackgroundSyncFailed(operationId = TAG, cause = throwable))
            emptyList()
        }
    }

    private fun closeIfShowCovered(sessionId: Long, closedAt: Long) {
        val coverage = rewatchSessionDao.sessionCoverage(sessionId) ?: return
        if (coverage.coversShow) {
            rewatchSessionDao.closeSession(sessionId = sessionId, closedAt = closedAt)
        }
    }

    private fun backfillSessions(showId: Long, sessions: List<RemoteRewatchSession>) {
        val localShowId = tvShowsDao.getLocalShowIdByTmdbId(showId) ?: return

        for (session in sessions) {
            val providerSessionId = session.providerSessionId ?: continue
            val startedAt = session.startedAt ?: session.lastWatchedAt ?: continue
            rewatchSessionDao.upsertProviderSession(
                showId = localShowId,
                providerSessionId = providerSessionId,
                startedAt = startedAt,
                closedAt = when (session.status) {
                    RemoteRewatchSessionStatus.ACTIVE -> null
                    RemoteRewatchSessionStatus.CLOSED,
                    RemoteRewatchSessionStatus.COMPLETED,
                    -> session.lastWatchedAt ?: startedAt
                },
            )
        }
    }

    private suspend fun drainUnsentEpisodes() {
        val source = activeSource() ?: return
        if (!source.supportsRewatch()) return

        for (episode in rewatchSessionDao.unsentEpisodes()) {
            try {
                val providerShowId = tvShowsDao.getTmdbIdForLocalShowId(episode.showId) ?: continue
                pushRewatch(
                    rowId = episode.rowId,
                    localSessionId = episode.sessionId,
                    providerShowId = providerShowId,
                    seasonNumber = episode.seasonNumber,
                    episodeNumber = episode.episodeNumber,
                    watchedAt = episode.watchedAt,
                )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                syncObserver.log(SyncError.BackgroundSyncFailed(operationId = TAG, cause = throwable))
            }
        }
    }

    private suspend fun pushRewatch(
        rowId: Long,
        localSessionId: Long,
        providerShowId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAt: Long,
    ) {
        val source = activeSource() ?: return
        try {
            val response = source.writeRewatch(
                RemoteRewatchWrite(
                    localSessionId = localSessionId,
                    providerShowId = providerShowId,
                    providerSessionId = rewatchSessionDao.sessionById(localSessionId)?.providerSessionId,
                    seasonNumber = seasonNumber,
                    episodeNumber = episodeNumber,
                    watchedAt = watchedAt,
                ),
            )
            when (response) {
                is ApiResponse.Success -> if (!response.body.skipped) {
                    response.body.providerSessionId?.let { providerSessionId ->
                        rewatchSessionDao.setProviderSessionId(sessionId = localSessionId, providerSessionId = providerSessionId)
                    }
                    rewatchSessionDao.markEpisodeSynced(rowId = rowId, syncedAt = dateTimeProvider.nowMillis())
                }
                is ApiResponse.Unauthenticated -> Unit
                is ApiResponse.Error -> syncObserver.log(SyncError.BackgroundSyncFailed(operationId = TAG, cause = response.toThrowable()))
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (throwable: Throwable) {
            syncObserver.log(SyncError.BackgroundSyncFailed(operationId = TAG, cause = throwable))
        }
    }

    private fun ApiResponse.Error<*>.toThrowable(): Throwable = when (this) {
        is ApiResponse.Error.HttpError -> Throwable("HTTP $code: $errorMessage")
        is ApiResponse.Error.SerializationError -> Throwable("Serialization error: $errorMessage")
        is ApiResponse.Error.NetworkFailure -> Throwable("Network failure: $kind", cause)
        is ApiResponse.Error.OfflineError -> Throwable(errorMessage)
    }

    private companion object {
        private const val TAG = "rewatch_sync"
    }
}
