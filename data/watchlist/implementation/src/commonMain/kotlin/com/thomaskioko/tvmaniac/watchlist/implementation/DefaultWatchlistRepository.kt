package com.thomaskioko.tvmaniac.watchlist.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.FollowedShows
import com.thomaskioko.tvmaniac.db.SearchFollowedShows
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.FOLLOWED_SHOWS_SYNC
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.shows.api.WatchlistRepository
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType.SHOWS_WATCHLISTED
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import org.mobilenativefoundation.store.store5.impl.extensions.get
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import kotlin.time.Duration

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultWatchlistRepository(
    private val watchlistDao: WatchlistDao,
    private val followedShowsDao: FollowedShowsDao,
    private val watchlistStore: WatchlistStore,
    private val datastoreRepository: DatastoreRepository,
    private val traktListDataSource: TraktListRemoteDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktAuthRepository: TraktAuthRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val logger: Logger,
) : WatchlistRepository {

    override fun observeWatchlist(): Flow<List<FollowedShows>> =
        watchlistDao.observeShowsInWatchlist().distinctUntilChanged()

    override fun searchWatchlistByQuery(query: String): Flow<List<SearchFollowedShows>> {
        return watchlistDao.observeWatchlistByQuery(query)
    }

    override fun observeListStyle(): Flow<Boolean> {
        return datastoreRepository.observeListStyle().map { listStyle ->
            listStyle == ListStyle.GRID
        }
    }

    override suspend fun saveListStyle(isGridMode: Boolean) {
        val listStyle = if (isGridMode) ListStyle.GRID else ListStyle.LIST
        datastoreRepository.saveListStyle(listStyle)
    }

    override suspend fun syncWatchlist(forceRefresh: Boolean) {
        val authState = traktAuthRepository.getAuthState()
        if (authState == null || !authState.isAuthorized) return

        processPendingUploadActions()
        processPendingDeleteActions()

        val watchlistChanged = traktActivityRepository.hasActivityChanged(SHOWS_WATCHLISTED)

        if (forceRefresh || watchlistChanged) {
            watchlistStore.fresh(Unit)
        } else {
            watchlistStore.get(Unit)
        }

        logger.debug(TAG, "Sync completed")
    }

    override suspend fun needsSync(expiry: Duration): Boolean =
        !requestManagerRepository.isRequestValid(
            requestType = FOLLOWED_SHOWS_SYNC.name,
            threshold = expiry,
        )

    private suspend fun processPendingUploadActions() {
        val pendingUploads = followedShowsDao.entriesWithUploadPendingAction()
        if (pendingUploads.isEmpty()) return

        val traktIds = pendingUploads.map { it.traktId }
        logger.debug(TAG, "Processing ${traktIds.size} pending uploads")

        for (traktId in traktIds) {
            when (val response = traktListDataSource.addShowToWatchListByTraktId(traktId)) {
                is ApiResponse.Success -> {
                    val _ = transactionRunner {
                        followedShowsDao.entryWithTraktId(traktId)?.let { entry ->
                            followedShowsDao.updatePendingAction(entry.id, PendingAction.NOTHING)
                        }
                    }
                }
                is ApiResponse.Error -> {
                    logger.error(TAG, "Failed to upload show $traktId: ${response.toErrorMessage()}")
                }
            }
        }
    }

    private suspend fun processPendingDeleteActions() {
        val pendingDeletes = followedShowsDao.entriesWithDeletePendingAction()
        if (pendingDeletes.isEmpty()) return

        val traktIds = pendingDeletes.map { it.traktId }
        logger.debug(TAG, "Processing ${traktIds.size} pending deletes")

        for (entry in pendingDeletes) {
            when (val response = traktListDataSource.removeShowFromWatchListByTraktId(entry.traktId)) {
                is ApiResponse.Success -> {
                    transactionRunner {
                        followedShowsDao.deleteById(entry.id)
                    }
                }
                is ApiResponse.Error -> {
                    logger.error(TAG, "Failed to delete show ${entry.traktId}: ${response.toErrorMessage()}")
                }
            }
        }
    }

    private fun ApiResponse.Error<*>.toErrorMessage(): String = when (this) {
        is ApiResponse.Error.HttpError -> "HTTP $code: $errorMessage"
        is ApiResponse.Error.SerializationError -> "Serialization error: $errorMessage"
        is ApiResponse.Error.GenericError -> errorMessage ?: "Unknown error"
    }

    private companion object {
        private const val TAG = "WatchlistRepository"
    }
}
