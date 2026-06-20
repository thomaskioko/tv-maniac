package com.thomaskioko.tvmaniac.continuewatching.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeTvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.api.ActivitySyncTypes
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeActivitySyncRepository
import com.thomaskioko.tvmaniac.trakt.api.model.EpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktNextEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUpNextNitroResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktUserRemoteDataSource
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class NitroContinueWatchingStoreTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val syncDataSource = FakeTraktSyncRemoteDataSource()
    private val userDataSource = FakeTraktUserRemoteDataSource()
    private val continueWatchingDao = FakeContinueWatchingDao()
    private val tvShowsDao = FakeTvShowsDao()
    private val requestManager = FakeRequestManagerRepository()
    private val checkpointStore = FakeActivitySyncRepository()
    private val dateTimeProvider = FakeDateTimeProvider(currentTime = NOW)
    private val transactionRunner = ImmediateTransactionRunner()
    private val logger = FakeLogger()

    private lateinit var nitroFetcher: NitroContinueWatchingFetcher
    private lateinit var store: NitroContinueWatchingStore

    @BeforeTest
    fun setUp() {
        nitroFetcher = NitroContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            syncRepository = checkpointStore,
            dateTimeProvider = dateTimeProvider,
            logger = logger,
        )
        store = NitroContinueWatchingStore(
            nitroFetcher = nitroFetcher,
            continueWatchingDao = continueWatchingDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = requestManager,
            syncRepository = checkpointStore,
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `should write nitro fetcher result to dao`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = false
        checkpointStore.setRemoteTimestamp(ActivityType.EPISODES_WATCHED, NOW)

        store.fetchWith(forceRefresh = true)

        continueWatchingDao.entriesObservable().test {
            awaitItem() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
            cancelAndIgnoreRemainingEvents()
        }
        syncDataSource.upNextNitroInvocations() shouldBe 1
        requestManager.upsertCalled shouldBe true
        checkpointStore.markSyncedToCalls() shouldContainExactlyInAnyOrder listOf(
            ActivitySyncTypes.NITRO_CONTINUE_WATCHING to ActivityType.EPISODES_WATCHED,
            ActivitySyncTypes.NITRO_CONTINUE_WATCHING to ActivityType.EPISODES_PAUSED,
        )
    }

    @Test
    fun `should delete rows missing from new payload given writer runs`() = runTest(testDispatcher) {
        continueWatchingDao.upsert(breakingBadEntry)
        continueWatchingDao.upsert(theWireEntry)
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = false

        store.fetchWith(forceRefresh = true)

        continueWatchingDao.entriesObservable().test {
            awaitItem() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should skip fetch given ttl valid and no activity change`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = true
        // No remote timestamp set, so isAheadOf returns false for both activity types.

        store.fetchWith(forceRefresh = false)

        syncDataSource.upNextNitroInvocations() shouldBe 0
        continueWatchingDao.entries().shouldBeEmpty()
    }

    @Test
    fun `should fetch given force refresh bypasses validator`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = true

        store.fetchWith(forceRefresh = true)

        syncDataSource.upNextNitroInvocations() shouldBe 1
        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should fetch given ttl stale`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = false

        store.fetchWith(forceRefresh = false)

        syncDataSource.upNextNitroInvocations() shouldBe 1
        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should fetch given activity changed`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = true
        // Remote timestamp present with no checkpoint -> isAheadOf returns true.
        checkpointStore.setRemoteTimestamp(ActivityType.EPISODES_WATCHED, NOW)

        store.fetchWith(forceRefresh = false)

        syncDataSource.upNextNitroInvocations() shouldBe 1
        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should leave dao unchanged given fetcher signals skip`() = runTest(testDispatcher) {
        continueWatchingDao.upsert(breakingBadEntry)
        checkpointStore.setCheckpoint(
            consumerId = ActivitySyncTypes.NITRO_CONTINUE_WATCHING,
            activityType = ActivityType.EPISODES_WATCHED,
            instant = NOW - 1.hours,
        )
        syncDataSource.setUpNextNitro(ApiResponse.Success(emptyList()))
        requestManager.requestValid = false

        try {
            store.fetchWith(forceRefresh = false)
        } catch (_: FetcherSkipSignal) {
            // expected: empty Nitro + fresh cursor = guard trips
        }

        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }
}

private class ImmediateTransactionRunner : DatabaseTransactionRunner {
    override fun <T> invoke(block: () -> T): T = block()
}

private val NOW: Instant = Instant.parse("2026-05-20T12:00:00Z")
private const val BREAKING_BAD_ID = 1388L
private const val THE_WIRE_ID = 1429L

private val breakingBadNitro = TraktUpNextNitroResponse(
    show = TraktShowResponse(
        title = "Breaking Bad",
        ids = ShowIds(trakt = BREAKING_BAD_ID, slug = "breaking-bad", tmdb = 1396),
        airedEpisodes = 62,
    ),
    progress = TraktWatchedProgressResponse(
        aired = 62,
        completed = 30,
        lastWatchedAt = "2026-05-10T20:15:00Z",
        nextEpisode = TraktNextEpisodeResponse(
            seasonNumber = 4,
            episodeNumber = 1,
            ids = EpisodeIds(trakt = 401, tmdb = null),
        ),
    ),
)

private val breakingBadEntry = ContinueWatchingEntry(
    showId = 1396L,
    tmdbId = 1396,
    airedEpisodes = 62,
    completedCount = 30,
    lastWatchedAt = Instant.parse("2026-05-10T20:15:00Z").toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse("2026-05-10T20:15:00Z").toEpochMilliseconds(),
    title = "Breaking Bad",
)

private val theWireEntry = ContinueWatchingEntry(
    showId = 1438L,
    tmdbId = 1438,
    airedEpisodes = 60,
    completedCount = 12,
    lastWatchedAt = Instant.parse("2026-04-22T09:00:00Z").toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse("2026-04-22T09:00:00Z").toEpochMilliseconds(),
    title = "The Wire",
)
