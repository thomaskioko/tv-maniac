package com.thomaskioko.tvmaniac.continuewatching.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.fresh
import com.thomaskioko.tvmaniac.core.networkutil.api.extensions.get
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class ContinueWatchingStoreTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val syncDataSource = FakeTraktSyncRemoteDataSource()
    private val continueWatchingDao = FakeContinueWatchingDao()
    private val requestManager = FakeRequestManagerRepository()
    private val activityRepository = FakeTraktActivityRepository()
    private val transactionRunner = ImmediateTransactionRunner()

    private lateinit var store: ContinueWatchingStore

    @BeforeTest
    fun setUp() {
        store = ContinueWatchingStore(
            traktSyncDataSource = syncDataSource,
            continueWatchingDao = continueWatchingDao,
            requestManagerRepository = requestManager,
            traktActivityRepository = activityRepository,
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `should write fetched rows to dao given fetch returns payload`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(breakingBadResponse, theWireResponse)))
        requestManager.requestValid = false

        store.fresh(key = Unit)

        continueWatchingDao.entriesObservable().test {
            awaitItem() shouldContainExactlyInAnyOrder listOf(breakingBadEntry, theWireEntry)
            cancelAndIgnoreRemainingEvents()
        }
        requestManager.upsertCalled shouldBe true
        activityRepository.getSyncedActivities() shouldBe setOf(ActivityType.EPISODES_WATCHED)
    }

    @Test
    fun `should delete rows missing from new payload given writer runs`() = runTest(testDispatcher) {
        continueWatchingDao.upsert(breakingBadEntry)
        continueWatchingDao.upsert(theWireEntry)

        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(breakingBadResponse)))
        requestManager.requestValid = false

        store.fresh(key = Unit)

        continueWatchingDao.entriesObservable().test {
            awaitItem() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should skip fetch given ttl valid and no activity change`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(breakingBadResponse)))
        requestManager.requestValid = true
        activityRepository.setActivityChanged(ActivityType.EPISODES_WATCHED, changed = false)

        store.get(key = Unit)

        syncDataSource.watchedShowsInvocations() shouldBe 0
        continueWatchingDao.entries().shouldBeEmpty()
    }

    @Test
    fun `should fetch given force refresh bypasses validator`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(breakingBadResponse)))
        requestManager.requestValid = true
        activityRepository.setActivityChanged(ActivityType.EPISODES_WATCHED, changed = false)

        store.fresh(key = Unit)

        syncDataSource.watchedShowsInvocations() shouldBe 1
        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should fetch given ttl stale`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(breakingBadResponse)))
        requestManager.requestValid = false
        activityRepository.setActivityChanged(ActivityType.EPISODES_WATCHED, changed = false)

        store.get(key = Unit)

        syncDataSource.watchedShowsInvocations() shouldBe 1
        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should fetch given activity changed`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(breakingBadResponse)))
        requestManager.requestValid = true
        activityRepository.setActivityChanged(ActivityType.EPISODES_WATCHED, changed = true)

        store.get(key = Unit)

        syncDataSource.watchedShowsInvocations() shouldBe 1
        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }
}

private class ImmediateTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}

private val breakingBadResponse = TraktWatchedShowResponse(
    plays = 56,
    lastWatchedAt = "2026-05-10T20:15:00Z",
    lastUpdatedAt = "2026-05-10T20:15:00Z",
    show = TraktShowResponse(
        title = "Breaking Bad",
        ids = ShowIds(trakt = 1388, slug = "breaking-bad", tmdb = 1396),
    ),
)

private val theWireResponse = TraktWatchedShowResponse(
    plays = 12,
    lastWatchedAt = "2026-04-22T09:00:00Z",
    lastUpdatedAt = "2026-04-22T09:00:00Z",
    show = TraktShowResponse(
        title = "The Wire",
        ids = ShowIds(trakt = 1429, slug = "the-wire", tmdb = 1438),
    ),
)

private val breakingBadEntry = ContinueWatchingEntry(
    traktId = 1388,
    tmdbId = 1396,
    airedEpisodes = 0L,
    completedCount = 56,
    lastWatchedAt = Instant.parse("2026-05-10T20:15:00Z").toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse("2026-05-10T20:15:00Z").toEpochMilliseconds(),
)

private val theWireEntry = ContinueWatchingEntry(
    traktId = 1429,
    tmdbId = 1438,
    airedEpisodes = 0L,
    completedCount = 12,
    lastWatchedAt = Instant.parse("2026-04-22T09:00:00Z").toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse("2026-04-22T09:00:00Z").toEpochMilliseconds(),
)
