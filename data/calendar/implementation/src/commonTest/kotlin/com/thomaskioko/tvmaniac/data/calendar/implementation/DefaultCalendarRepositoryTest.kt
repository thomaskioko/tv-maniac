package com.thomaskioko.tvmaniac.data.calendar.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.data.calendar.CalendarRemoteDataSource
import com.thomaskioko.tvmaniac.data.calendar.RemoteCalendarEntry
import com.thomaskioko.tvmaniac.data.calendar.testing.FakeCalendarRemoteDataSource
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.followedshows.api.FollowedShowEntry
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsDao
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeTvShowsDao
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultCalendarRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val followedShowsDao = FakeFollowedShowsDao()
    private val accountManager = FakeAccountManager()
    private val requestManagerRepository = FakeRequestManagerRepository(initialRequestValid = false)
    private val tvShowsDao = FakeTvShowsDao()
    private val syncObserver = FakeSyncObserver()

    private lateinit var calendarDao: DefaultCalendarDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        calendarDao = DefaultCalendarDao(database, showIdResolver, dispatchers)
        seedShow()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should emit entries given followed shows become non-empty after login`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.SIMKL)
        val repository = buildRepository()

        repository.observeCalendarEntries(START_EPOCH, END_EPOCH).test {
            awaitItem().shouldBeEmpty()

            followedShowsDao.setEntries(listOf(followedEntry()))

            var entries = awaitItem()
            while (entries.isEmpty()) entries = awaitItem()
            entries shouldHaveSize 1
            entries.first().showId shouldBe TMDB_ID

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should not fetch given no active provider`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(null)
        val repository = buildRepository()

        val job = launch { repository.observeCalendarEntries(START_EPOCH, END_EPOCH).collect {} }
        followedShowsDao.setEntries(listOf(followedEntry()))
        advanceUntilIdle()

        calendarDao.hasEntriesInRange(START_EPOCH, END_EPOCH) shouldBe false

        job.cancel()
    }

    @Test
    fun `should report background sync error given fetch fails`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(AccountProvider.SIMKL)
        val repository = buildRepository(
            activeSource = {
                FakeCalendarRemoteDataSource(
                    provider = AccountProvider.SIMKL,
                    calendarResponse = ApiResponse.Error.NetworkFailure(
                        kind = ApiResponse.Error.NetworkFailure.Kind.Connectivity,
                    ),
                )
            },
        )

        val observeJob = launch { repository.observeCalendarEntries(START_EPOCH, END_EPOCH).collect {} }

        syncObserver.errors.test {
            awaitItem().shouldBeInstanceOf<SyncError.BackgroundSyncFailed>()
        }

        observeJob.cancel()
    }

    private fun buildRepository(
        activeSource: () -> CalendarRemoteDataSource? = ::simklSource,
    ): DefaultCalendarRepository {
        val store = CalendarStore(
            activeSource = activeSource,
            calendarDao = calendarDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = requestManagerRepository,
            databaseTransactionRunner = ImmediateTransactionRunner(),
            dispatchers = dispatchers,
        )
        return DefaultCalendarRepository(
            store = store,
            calendarDao = calendarDao,
            followedShowsDao = followedShowsDao,
            accountManager = accountManager,
            syncObserver = syncObserver,
        )
    }

    private fun simklSource(): CalendarRemoteDataSource = FakeCalendarRemoteDataSource(
        provider = AccountProvider.SIMKL,
        calendarResponse = if (followedShowsDao.entries().isEmpty()) {
            ApiResponse.Success(emptyList())
        } else {
            ApiResponse.Success(listOf(remoteEntry()))
        },
    )

    private fun remoteEntry(): RemoteCalendarEntry = RemoteCalendarEntry(
        tmdbId = TMDB_ID,
        episodeTraktId = null,
        showTitle = "Breaking Bad",
        episodeTitle = "Pilot",
        seasonNumber = 1,
        episodeNumber = 1,
        firstAiredIso = "1970-01-02T00:00:00Z",
        runtime = 45,
        overview = null,
        rating = null,
        votes = null,
    )

    private fun followedEntry(): FollowedShowEntry = FollowedShowEntry(
        id = 1L,
        showId = TMDB_ID,
        tmdbId = TMDB_ID,
        followedAt = Instant.fromEpochMilliseconds(0),
    )

    private fun seedShow() {
        database.tvShowQueries.upsert(
            tmdb_id = Id(TMDB_ID),
            name = "Breaking Bad",
            overview = "",
            language = "en",
            year = "2024",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        showIdForTraktId(TMDB_ID)
    }

    private class ImmediateTransactionRunner : DatabaseTransactionRunner {
        override fun <T> invoke(block: () -> T): T = block()
    }

    private companion object {
        private const val TMDB_ID = 1L
        private const val START_EPOCH = 0L
        private const val END_EPOCH = 7L * 24 * 60 * 60 * 1000
    }
}
