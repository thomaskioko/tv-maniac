package com.thomaskioko.tvmaniac.data.library.implementation

import com.thomaskioko.tvmaniac.core.logger.Logger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.library.LibraryDao
import com.thomaskioko.tvmaniac.data.library.LibraryRepository
import com.thomaskioko.tvmaniac.data.library.model.LibraryItem
import com.thomaskioko.tvmaniac.data.library.model.LibrarySortOption
import com.thomaskioko.tvmaniac.data.library.model.WatchProvider
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.datastore.api.ListStyle
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.LibraryShows
import com.thomaskioko.tvmaniac.db.WatchProvidersForShow
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestManagerRepository
import com.thomaskioko.tvmaniac.resourcemanager.api.RequestTypeConfig.WATCHLIST_SYNC
import com.thomaskioko.tvmaniac.syncactivity.api.TraktActivityRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType.SHOWS_WATCHLISTED
import com.thomaskioko.tvmaniac.trakt.api.TraktListRemoteDataSource
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthRepository
import com.thomaskioko.tvmaniac.util.api.FormatterUtil
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
public class DefaultLibraryRepository(
    private val libraryDao: LibraryDao,
    private val libraryStore: LibraryStore,
    private val datastoreRepository: DatastoreRepository,
    private val followedShowsDao: FollowedShowsDao,
    private val traktListDataSource: TraktListRemoteDataSource,
    private val requestManagerRepository: RequestManagerRepository,
    private val traktAuthRepository: TraktAuthRepository,
    private val traktActivityRepository: TraktActivityRepository,
    private val transactionRunner: DatabaseTransactionRunner,
    private val formatterUtil: FormatterUtil,
    private val logger: Logger,
) : LibraryRepository {

    override fun observeLibrary(
        query: String,
        sortOption: LibrarySortOption,
        followedOnly: Boolean,
    ): Flow<List<LibraryItem>> {
        return if (query.isBlank()) {
            libraryDao.observeLibrary(followedOnly = followedOnly)
        } else {
            libraryDao.searchLibrary(query)
        }
            .distinctUntilChanged()
            .map { shows ->
                shows.map { show ->
                    val providers = libraryDao.getWatchProviders(show.show_tmdb_id.id)
                        .map { it.toWatchProvider() }
                    show.toLibraryItem(providers)
                }
            }
            .map { items -> items.applySorting(sortOption) }
    }

    private fun List<LibraryItem>.applySorting(sortOption: LibrarySortOption): List<LibraryItem> {
        return when (sortOption) {
            LibrarySortOption.LAST_WATCHED_DESC ->
                sortedByDescending { it.lastWatchedAt ?: it.followedAt ?: 0L }
            LibrarySortOption.LAST_WATCHED_ASC ->
                sortedBy { it.lastWatchedAt ?: it.followedAt ?: Long.MAX_VALUE }
            LibrarySortOption.NEW_EPISODES ->
                sortedByDescending { it.totalCount - it.watchedCount }
            LibrarySortOption.EPISODES_LEFT_DESC ->
                sortedByDescending { it.totalCount - it.watchedCount }
            LibrarySortOption.EPISODES_LEFT_ASC ->
                sortedBy { it.totalCount - it.watchedCount }
            LibrarySortOption.ALPHABETICAL ->
                sortedBy { it.title.lowercase() }
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
                ?: LibrarySortOption.LAST_WATCHED_DESC
        }
    }

    override suspend fun saveSortOption(sortOption: LibrarySortOption) {
        datastoreRepository.saveLibrarySortOption(sortOption.name)
    }

    private fun LibraryShows.toLibraryItem(watchProviders: List<WatchProvider>): LibraryItem =
        LibraryItem(
            traktId = show_trakt_id.id,
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

    private fun WatchProvidersForShow.toWatchProvider(): WatchProvider = WatchProvider(
        id = provider_id.id,
        name = name,
        logoPath = logo_path?.let { formatterUtil.formatTmdbPosterPath(it) },
    )

    override suspend fun syncLibrary(forceRefresh: Boolean) {
        val authState = traktAuthRepository.getAuthState()
        if (authState == null || !authState.isAuthorized) return

        processPendingUploadActions()
        processPendingDeleteActions()

        val watchlistChanged = traktActivityRepository.hasActivityChanged(SHOWS_WATCHLISTED)

        if (forceRefresh || watchlistChanged) {
            libraryStore.fresh(Unit)
        } else {
            libraryStore.get(Unit)
        }

        logger.debug(TAG, "Sync completed")
    }

    override suspend fun needsSync(expiry: Duration): Boolean =
        !requestManagerRepository.isRequestValid(
            requestType = WATCHLIST_SYNC.name,
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
        private const val TAG = "LibraryRepository"
    }
}
