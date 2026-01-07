package com.thomaskioko.tvmaniac.followedshows.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsRepository
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
import com.thomaskioko.tvmaniac.util.api.DateTimeProvider
import com.thomaskioko.tvmaniac.util.api.ItemSyncer
import com.thomaskioko.tvmaniac.util.api.syncerForEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultFollowedShowsRepository(
    private val followedShowsDao: FollowedShowsDao,
    private val dataSource: FollowedShowsDataSource,
    private val lastRequestStore: FollowedShowsLastRequestStore,
    private val traktAuthRepository: TraktAuthRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val dateTimeProvider: DateTimeProvider,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : FollowedShowsRepository {

    private val syncer: ItemSyncer<FollowedShowEntry, FollowedShowEntry, Long> = syncerForEntity(
        upsertEntity = { entry -> val _ = followedShowsDao.upsert(entry) },
        deleteEntity = { entry -> followedShowsDao.deleteById(entry.id) },
        entityToKey = { it.tmdbId },
        mapper = { networkEntity, currentEntity ->
            networkEntity.copy(id = currentEntity?.id ?: 0)
        },
    )

    override suspend fun syncFollowedShows(forceRefresh: Boolean) {
        val authState = traktAuthRepository.state.first()
        if (authState != TraktAuthState.LOGGED_IN) return

        processPendingUploadActions()
        processPendingDeleteActions()

        if (forceRefresh || lastRequestStore.isRequestExpired()) {
            fetchRemoteWatchlist()
            lastRequestStore.updateLastRequest()
            logger.debug(TAG, "Sync completed (pulled remote)")
        } else {
            logger.debug(TAG, "Sync skipped (cache valid)")
        }
    }

    override fun observeFollowedShows(): Flow<List<FollowedShowEntry>> =
        followedShowsDao.entriesObservable()

    override suspend fun addFollowedShow(tmdbId: Long) {
        withContext(dispatchers.io) {
            transactionRunner {
                val existingEntry = followedShowsDao.entryWithTmdbId(tmdbId)
                if (existingEntry == null || existingEntry.pendingAction == PendingAction.DELETE) {
                    val _ = followedShowsDao.upsert(
                        FollowedShowEntry(
                            id = existingEntry?.id ?: 0,
                            tmdbId = tmdbId,
                            followedAt = dateTimeProvider.now(),
                            pendingAction = PendingAction.UPLOAD,
                            traktId = existingEntry?.traktId,
                        ),
                    )
                    logger.debug(TAG, "Marked show $tmdbId for upload")
                }
            }
        }
        syncFollowedShows()
    }

    override suspend fun removeFollowedShow(tmdbId: Long) {
        withContext(dispatchers.io) {
            transactionRunner {
                followedShowsDao.entryWithTmdbId(tmdbId)?.also { entry ->
                    if (entry.traktId != null) {
                        val _ = followedShowsDao.upsert(entry.copy(pendingAction = PendingAction.DELETE))
                        logger.debug(TAG, "Marked show $tmdbId for deletion")
                    } else {
                        followedShowsDao.deleteById(entry.id)
                        logger.debug(TAG, "Deleted local entry for show $tmdbId")
                    }
                }
            }
        }
        syncFollowedShows()
    }

    override suspend fun needsSync(expiry: Duration): Boolean =
        lastRequestStore.isRequestExpired(expiry)

    private suspend fun processPendingUploadActions() {
        val pending = withContext(dispatchers.io) {
            followedShowsDao.entriesWithUploadPendingAction()
        }
        if (pending.isEmpty()) return

        logger.debug(TAG, "Processing ${pending.size} pending shows")

        dataSource.addShowsToWatchlist(pending.map { it.tmdbId })

        withContext(dispatchers.io) {
            transactionRunner {
                pending.forEach { entry ->
                    followedShowsDao.updatePendingAction(entry.id, PendingAction.NOTHING)
                }
            }
        }
    }

    private suspend fun processPendingDeleteActions() {
        val pending = withContext(dispatchers.io) {
            followedShowsDao.entriesWithDeletePendingAction()
        }
        if (pending.isEmpty()) return

        logger.debug(TAG, "Processing ${pending.size} pending deletions")

        dataSource.removeShowsFromWatchlist(pending.map { it.tmdbId })

        withContext(dispatchers.io) {
            transactionRunner {
                pending.forEach { entry ->
                    followedShowsDao.deleteById(entry.id)
                }
            }
        }
    }

    private suspend fun fetchRemoteWatchlist() {
        val remoteEntries = dataSource.getFollowedShows()

        withContext(dispatchers.io) {
            transactionRunner {
                syncer.sync(
                    currentValues = followedShowsDao.entriesWithNoPendingAction(),
                    networkValues = remoteEntries.map { (entry, _) -> entry },
                )
            }
        }
    }

    private companion object {
        private const val TAG = "FollowedShowsRepository"
    }
}
