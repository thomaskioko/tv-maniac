package com.thomaskioko.tvmaniac.data.library.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncError
import com.thomaskioko.tvmaniac.core.networkutil.api.model.SyncException
import com.thomaskioko.tvmaniac.data.library.LibraryRemoteDataSource
import com.thomaskioko.tvmaniac.data.library.model.RemoteFollowedShow
import com.thomaskioko.tvmaniac.data.library.model.WatchlistShowIds
import com.thomaskioko.tvmaniac.data.library.model.WatchlistSyncResult
import com.thomaskioko.tvmaniac.data.library.testing.FakeLibraryRemoteDataSource
import com.thomaskioko.tvmaniac.data.watchproviders.implementation.DefaultWatchProviderDao
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.DbTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowsDao
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.followedshows.implementation.DefaultFollowedShowsDao
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.implementation.DefaultShowReconciler
import com.thomaskioko.tvmaniac.shows.implementation.DefaultTvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbShowDetailsNetworkDataSource
import com.thomaskioko.tvmaniac.tmdb.testing.FakeTmdbShowsNetworkDataSource
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import com.thomaskioko.tvmaniac.util.testing.FakeFormatterUtil
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant
import com.thomaskioko.tvmaniac.syncstate.api.SyncError as SyncStateError

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultLibraryRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val fakeAccountManager = FakeAccountManager()
    private val fakeSyncObserver = FakeSyncObserver()
    private val fakeRemoteSource = FakeLibraryRemoteDataSource()

    private lateinit var followedShowsDao: FollowedShowsDao
    private lateinit var repository: DefaultLibraryRepository
    private var activeSource: LibraryRemoteDataSource = fakeRemoteSource

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAccountManager.setActiveProvider(SyncProviderSource.TRAKT)
        val tvShowsDao = DefaultTvShowsDao(database = database, dispatchers = dispatchers)
        followedShowsDao = DefaultFollowedShowsDao(
            database = database,
            showIdResolver = showIdResolver,
            dispatchers = dispatchers,
        )
        val requestManager = FakeRequestManagerRepository(initialRequestValid = false)
        val transactionRunner = DbTransactionRunner(database)
        val formatterUtil = FakeFormatterUtil()
        val libraryStore = LibraryStore(
            activeSource = { activeSource },
            tmdbDataSource = FakeTmdbShowDetailsNetworkDataSource(),
            followedShowsDao = followedShowsDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = requestManager,
            syncRepository = FakeActivitySyncRepository(),
            transactionRunner = transactionRunner,
            showReconciler = DefaultShowReconciler(
                tmdbDataSource = FakeTmdbShowsNetworkDataSource(),
                database = database,
                logger = FakeLogger(),
            ),
            formatterUtil = formatterUtil,
            dispatchers = dispatchers,
        )
        repository = DefaultLibraryRepository(
            libraryDao = DefaultLibraryDao(
                database = database,
                dispatchers = dispatchers,
                dateTimeProvider = FakeDateTimeProvider(),
            ),
            watchProviderDao = DefaultWatchProviderDao(
                database = database,
                showIdResolver = showIdResolver,
                dispatcher = dispatchers,
            ),
            libraryStore = libraryStore,
            datastoreRepository = FakeDatastoreRepository(),
            followedShowsDao = followedShowsDao,
            activeSource = { activeSource },
            accountManager = fakeAccountManager,
            requestManagerRepository = requestManager,
            transactionRunner = transactionRunner,
            formatterUtil = formatterUtil,
            syncObserver = fakeSyncObserver,
            logger = FakeLogger(),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should keep show followed given follow is uploaded while library fetch is in flight`() = runTest(testDispatcher) {
        val gate = CompletableDeferred<Unit>()
        activeSource = GatedLibrarySource(gate)

        val fetch = launch { repository.syncLibrary(forceRefresh = true) }
        advanceUntilIdle()

        insertFollowedShow(tmdbId = TMDB_ID, pendingAction = PendingAction.UPLOAD)
        val push = launch { repository.syncPendingFollowedShows() }
        advanceUntilIdle()

        followedShowsDao.entryWithTmdbId(TMDB_ID)?.pendingAction shouldBe PendingAction.UPLOAD

        gate.complete(Unit)
        advanceUntilIdle()
        fetch.join()
        push.join()

        followedShowsDao.entryWithTmdbId(TMDB_ID).shouldNotBeNull()
        followedShowsDao.entryWithTmdbId(TMDB_ID)?.pendingAction shouldBe PendingAction.NOTHING
    }

    @Test
    fun `should log not found error given upload response has not found shows`() = runTest(testDispatcher) {
        fakeRemoteSource.setAddResponse(ApiResponse.Success(WatchlistSyncResult(notFoundCount = 1)))
        insertFollowedShow(tmdbId = TMDB_ID, pendingAction = PendingAction.UPLOAD)

        fakeSyncObserver.errors.test {
            repository.syncPendingFollowedShows()

            val error = awaitItem().shouldBeInstanceOf<SyncStateError.BackgroundSyncFailed>()
            error.operationId shouldBe "LibraryRepository:upload"
            val syncError = error.cause.shouldBeInstanceOf<SyncException>().syncError
            syncError.shouldBeInstanceOf<SyncError.Permanent.NotFound>()
        }

        followedShowsDao.entryWithTmdbId(TMDB_ID)?.pendingAction shouldBe PendingAction.NOTHING
    }

    private fun insertFollowedShow(tmdbId: Long, pendingAction: PendingAction) {
        database.tvShowQueries.upsert(
            tmdb_id = Id<TmdbId>(tmdbId),
            name = SHOW_TITLE,
            overview = "",
            language = null,
            year = null,
            ratings = 0.0,
            vote_count = 0,
            genres = null,
            status = null,
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        showIdForTraktId(traktId = TRAKT_ID, tmdbId = tmdbId)
        followedShowsDao.upsert(
            FollowedShowEntry(
                showId = tmdbId,
                tmdbId = tmdbId,
                followedAt = FOLLOWED_AT,
                pendingAction = pendingAction,
            ),
        )
    }

    private companion object {
        private const val TMDB_ID = 42L
        private const val TRAKT_ID = 1001L
        private const val SHOW_TITLE = "Test Show"
        private val FOLLOWED_AT = Instant.parse("2025-03-01T00:00:00Z")
    }
}

private class GatedLibrarySource(
    private val gate: CompletableDeferred<Unit>,
) : LibraryRemoteDataSource {

    override val provider: SyncProviderSource = SyncProviderSource.TRAKT

    override suspend fun getWatchlist(): ApiResponse<List<RemoteFollowedShow>> {
        gate.await()
        return ApiResponse.Success(emptyList())
    }

    override suspend fun addToWatchlist(shows: List<WatchlistShowIds>): ApiResponse<WatchlistSyncResult> =
        ApiResponse.Success(WatchlistSyncResult(notFoundCount = 0))

    override suspend fun removeFromWatchlist(shows: List<WatchlistShowIds>): ApiResponse<WatchlistSyncResult> =
        ApiResponse.Success(WatchlistSyncResult(notFoundCount = 0))
}
