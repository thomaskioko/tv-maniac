package com.thomaskioko.tvmaniac.data.library.implementation

import com.thomaskioko.tvmaniac.accountmanager.api.AccountManager
import com.thomaskioko.tvmaniac.accountmanager.api.getActiveProvider
import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncException
import com.thomaskioko.tvmaniac.core.networkutil.api.model.toSyncError
import com.thomaskioko.tvmaniac.data.library.LibraryDao
import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.data.library.model.WatchProvider
import com.thomaskioko.tvmaniac.data.watchproviders.api.WatchProviderDao
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.LibraryShows
import com.thomaskioko.tvmaniac.db.WatchProviders
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.LIBRARY_SYNC
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlin.time.Duration
import com.thomaskioko.tvmaniac.syncstate.api.SyncError as SyncStateError

@OptIn(ExperimentalCoroutinesApi::class)
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
public class DefaultLibraryRepository(
    private val libraryDao: LibraryDao,
    private val watchProviderDao: WatchProviderDao,
    private val libraryStore: LibraryStore,
    private val datastoreRepository: DatastoreRepository,
    private val followedShowsDao: FollowedShowsDao,
    private val sources: Set<LibraryRemoteDataSource>,
    private val accountManager: AccountManager,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktAuthRepository: TraktAuthRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val formatterUtil: FormatterUtil,
    private val syncObserver: SyncObserver,
    private val logger: Logger,
) : LibraryRepository {

    override fun observeLibrary(
        query: String,
        sortOption: LibrarySortOption,
        followedOnly: Boolean,
    ): Flow<List<LibraryItem>> {
        val showsFlow = if (query.isBlank()) {
            libraryDao.observeLibrary(followedOnly = followedOnly)
        } else {
            libraryDao.searchLibrary(query)
        }

        return showsFlow
            .distinctUntilChanged()
            .flatMapLatest { shows -> observeShowsWithProviders(shows) }
            .map { items -> items.applySorting(sortOption) }
    }

    private fun observeShowsWithProviders(shows: List<LibraryShows>): Flow<List<LibraryItem>> {
        if (shows.isEmpty()) return flowOf(emptyList())

        val providerFlows = shows.map { show ->
            watchProviderDao.observeWatchProviders(show.show_tmdb_id.id)
        }

        return combine(providerFlows) { providerArrays ->
            shows.mapIndexed { index, show ->
                val providers = providerArrays[index].map { it.toWatchProvider() }
                show.toLibraryItem(providers)
            }
        }
    }

    private fun List<LibraryItem>.applySorting(sortOption: LibrarySortOption): List<LibraryItem> {
        return when (sortOption) {
            LibrarySortOption.RANK_ASC -> this
            LibrarySortOption.RANK_DESC -> reversed()
            LibrarySortOption.ADDED_DESC -> sortedByDescending { it.followedAt ?: 0L }
            LibrarySortOption.ADDED_ASC -> sortedBy { it.followedAt ?: Long.MAX_VALUE }
            LibrarySortOption.RELEASED_DESC -> sortedByDescending { it.year }
            LibrarySortOption.RELEASED_ASC -> sortedBy { it.year }
            LibrarySortOption.TITLE_ASC -> sortedBy { it.title.lowercase() }
            LibrarySortOption.TITLE_DESC -> sortedByDescending { it.title.lowercase() }
        }
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

    override fun observeSortOption(): Flow<LibrarySortOption> {
        return datastoreRepository.observeLibrarySortOption().map { sortOptionName ->
            LibrarySortOption.entries.find { it.name == sortOptionName }
                ?: LibrarySortOption.ADDED_DESC
        }
    }

    override suspend fun saveSortOption(sortOption: LibrarySortOption) {
        datastoreRepository.saveLibrarySortOption(sortOption.name)
    }

    private fun LibraryShows.toLibraryItem(watchProviders: List<WatchProvider>): LibraryItem =
        LibraryItem(
            showId = show_trakt_id,
            tmdbId = show_tmdb_id.id,
            title = title,
            posterPath = poster_path,
            status = status,
            year = year,
            rating = ratings,
            genres = genres,
            seasonCount = season_count,
            episodeCount = episode_count,
            watchedCount = watched_count,
            totalCount = total_count,
            lastWatchedAt = last_watched_at,
            followedAt = followed_at,
            isFollowed = is_followed == 1L,
            watchProviders = watchProviders,
        )

    private fun WatchProviders.toWatchProvider(): WatchProvider = WatchProvider(
        id = provider_id.id,
        name = name,
        logoPath = logo_path?.let { formatterUtil.formatTmdbPosterPath(it) },
    )

    override suspend fun syncLibrary(forceRefresh: Boolean) {
        val authState = traktAuthRepository.getAuthState()
        if (authState == null || !authState.isAuthorized) return

        if (flushPendingFollowActions() == PendingActionOutcome.BACK_OFF) return

        val sortOption = currentSortOption()
        when {
            forceRefresh -> libraryStore.fresh(sortOption) { logger.debug(TAG, it) }
            else -> libraryStore.get(sortOption) { logger.debug(TAG, it) }
        }

        logger.debug(TAG, "Sync completed")
    }

    override suspend fun syncPendingFollowedShows() {
        val authState = traktAuthRepository.getAuthState()
        if (authState == null || !authState.isAuthorized) return

        val _ = flushPendingFollowActions()
    }

    private suspend fun flushPendingFollowActions(): PendingActionOutcome {
        if (processPendingUploadActions() == PendingActionOutcome.BACK_OFF) return PendingActionOutcome.BACK_OFF
        if (processPendingDeleteActions() == PendingActionOutcome.BACK_OFF) return PendingActionOutcome.BACK_OFF
        return PendingActionOutcome.CONTINUE
    }

    override suspend fun needsSync(expiry: Duration): Boolean =
        !requestManagerRepository.isRequestValid(
            requestType = LIBRARY_SYNC.name,
            threshold = expiry,
        )

    private fun activeSource(): LibraryRemoteDataSource? =
        sources.getActiveProvider(accountManager)

    private suspend fun processPendingUploadActions(): PendingActionOutcome {
        val pendingUploads = followedShowsDao.entriesWithUploadPendingAction()
        if (pendingUploads.isEmpty()) return PendingActionOutcome.CONTINUE

        val source = activeSource() ?: return PendingActionOutcome.BACK_OFF
        val showIds = pendingUploads.map { it.showId }
        logger.debug(TAG, "Processing ${showIds.size} pending uploads")

        return when (val response = source.addToWatchlist(showIds)) {
            is ApiResponse.Success -> {
                val notFoundCount = response.body.notFoundCount
                transactionRunner {
                    pendingUploads.forEach { entry ->
                        followedShowsDao.updatePendingAction(entry.id, PendingAction.NOTHING)
                    }
                }
                if (notFoundCount > 0) {
                    logger.debug(TAG, "Cleared $notFoundCount unknown ids on upload (Trakt not_found)")
                }
                PendingActionOutcome.CONTINUE
            }
            is ApiResponse.Unauthenticated -> PendingActionOutcome.BACK_OFF
            is ApiResponse.Error -> handleBatchError(action = "upload", showIds = showIds, error = response)
        }
    }

    private suspend fun processPendingDeleteActions(): PendingActionOutcome {
        val pendingDeletes = followedShowsDao.entriesWithDeletePendingAction()
        if (pendingDeletes.isEmpty()) return PendingActionOutcome.CONTINUE

        val source = activeSource() ?: return PendingActionOutcome.BACK_OFF
        val showIds = pendingDeletes.map { it.showId }
        logger.debug(TAG, "Processing ${showIds.size} pending deletes")

        return when (val response = source.removeFromWatchlist(showIds)) {
            is ApiResponse.Success -> {
                val notFoundCount = response.body.notFoundCount
                transactionRunner {
                    pendingDeletes.forEach { entry ->
                        followedShowsDao.deleteById(entry.id)
                    }
                }
                if (notFoundCount > 0) {
                    logger.debug(TAG, "Cleared $notFoundCount unknown ids on delete (Trakt not_found)")
                }
                PendingActionOutcome.CONTINUE
            }
            is ApiResponse.Unauthenticated -> PendingActionOutcome.BACK_OFF
            is ApiResponse.Error -> handleBatchError(action = "delete", showIds = showIds, error = response)
        }
    }

    private fun handleBatchError(
        action: String,
        showIds: List<Long>,
        error: ApiResponse.Error<*>,
    ): PendingActionOutcome {
        val message = error.toErrorMessage()
        return when (val syncError = error.toSyncError()) {
            is SyncError.Retryable -> {
                logger.error(TAG, "Backing off $action for ${showIds.size} shows: $message ($syncError)")
                syncObserver.log(
                    SyncStateError.BackgroundSyncFailed(
                        operationId = "$TAG:$action",
                        cause = SyncException(syncError),
                    ),
                )
                PendingActionOutcome.BACK_OFF
            }
            is SyncError.Permanent -> {
                logger.error(TAG, "$action permanently failed for ${showIds.size} shows: $message ($syncError)")
                if (syncError is SyncError.Permanent.AccountLimitExceeded) {
                    syncObserver.log(
                        SyncStateError.AccountLimitExceeded(
                            message = syncError.message,
                            cause = SyncException(syncError),
                        ),
                    )
                } else {
                    syncObserver.log(
                        SyncStateError.BackgroundSyncFailed(
                            operationId = "$TAG:$action",
                            cause = SyncException(syncError),
                        ),
                    )
                }
                PendingActionOutcome.BACK_OFF
            }
            is SyncError.Unknown -> {
                logger.error(TAG, "$action failed for ${showIds.size} shows: $message")
                PendingActionOutcome.CONTINUE
            }
        }
    }

    private fun ApiResponse.Error<*>.toErrorMessage(): String = when (this) {
        is ApiResponse.Error.HttpError -> "HTTP $code: $errorMessage"
        is ApiResponse.Error.SerializationError -> "Serialization error: $errorMessage"
        is ApiResponse.Error.NetworkFailure -> "Network failure: $kind (${cause?.message ?: "no detail"})"
        is ApiResponse.Error.OfflineError -> "No internet connection"
    }

    private suspend fun currentSortOption(): LibrarySortOption {
        val name = datastoreRepository.observeLibrarySortOption().first()
        return LibrarySortOption.entries.find { it.name == name }
            ?: LibrarySortOption.ADDED_DESC
    }

    private enum class PendingActionOutcome { CONTINUE, BACK_OFF }

    private companion object {
        private const val TAG = "LibraryRepository"
    }
}
