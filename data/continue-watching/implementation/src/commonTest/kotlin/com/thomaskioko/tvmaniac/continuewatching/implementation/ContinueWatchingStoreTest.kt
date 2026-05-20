package com.thomaskioko.tvmaniac.continuewatching.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeTvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.api.model.ActivityType
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.model.EpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktNextEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
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
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class ContinueWatchingStoreTest {

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
    private val activityRepository = FakeTraktActivityRepository()
    private val dateTimeProvider = FakeDateTimeProvider(currentTime = NOW)
    private val transactionRunner = ImmediateTransactionRunner()
    private val logger = FakeLogger()

    private lateinit var progressFetcher: ProgressContinueWatchingFetcher
    private lateinit var nitroFetcher: NitroContinueWatchingFetcher
    private lateinit var store: ContinueWatchingStore

    @BeforeTest
    fun setUp() {
        progressFetcher = ProgressContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
            continueWatchingDao = continueWatchingDao,
            tvShowsDao = tvShowsDao,
            transactionRunner = transactionRunner,
            datastoreRepository = FakeDatastoreRepository(),
            logger = logger,
        )
        nitroFetcher = NitroContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
            dateTimeProvider = dateTimeProvider,
            logger = logger,
        )
        store = ContinueWatchingStore(
            progressFetcher = progressFetcher,
            nitroFetcher = nitroFetcher,
            continueWatchingDao = continueWatchingDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = requestManager,
            traktActivityRepository = activityRepository,
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
        )
    }

    @Test
    fun `should write progress fetcher result to dao given progress key`() = runTest(testDispatcher) {
        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(listOf(breakingBadPlayback)))
        syncDataSource.setShowWatchedProgress(BREAKING_BAD_ID, ApiResponse.Success(breakingBadProgress))
        requestManager.requestValid = false

        store.fetchWith(key = ContinueWatchingKey.Progress, forceRefresh = true)

        continueWatchingDao.entriesObservable().test {
            awaitItem() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
            cancelAndIgnoreRemainingEvents()
        }
        requestManager.upsertCalled shouldBe true
        activityRepository.getSyncedActivities() shouldBe setOf(ActivityType.EPISODES_WATCHED)
        syncDataSource.playbackEpisodesInvocations() shouldBe 1
        syncDataSource.upNextNitroInvocations() shouldBe 0
    }

    @Test
    fun `should write nitro fetcher result to dao given nitro key`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = false

        store.fetchWith(key = ContinueWatchingKey.Nitro, forceRefresh = true)

        continueWatchingDao.entriesObservable().test {
            awaitItem() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
            cancelAndIgnoreRemainingEvents()
        }
        syncDataSource.upNextNitroInvocations() shouldBe 1
        syncDataSource.playbackEpisodesInvocations() shouldBe 0
    }

    @Test
    fun `should delete rows missing from new payload given writer runs`() = runTest(testDispatcher) {
        continueWatchingDao.upsert(breakingBadEntry)
        continueWatchingDao.upsert(theWireEntry)
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = false

        store.fetchWith(key = ContinueWatchingKey.Nitro, forceRefresh = true)

        continueWatchingDao.entriesObservable().test {
            awaitItem() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should skip fetch given ttl valid and no activity change`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = true
        activityRepository.setActivityChanged(ActivityType.EPISODES_WATCHED, changed = false)

        store.fetchWith(key = ContinueWatchingKey.Nitro, forceRefresh = false)

        syncDataSource.upNextNitroInvocations() shouldBe 0
        continueWatchingDao.entries().shouldBeEmpty()
    }

    @Test
    fun `should fetch given force refresh bypasses validator`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = true
        activityRepository.setActivityChanged(ActivityType.EPISODES_WATCHED, changed = false)

        store.fetchWith(key = ContinueWatchingKey.Nitro, forceRefresh = true)

        syncDataSource.upNextNitroInvocations() shouldBe 1
        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should fetch given ttl stale`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = false
        activityRepository.setActivityChanged(ActivityType.EPISODES_WATCHED, changed = false)

        store.fetchWith(key = ContinueWatchingKey.Nitro, forceRefresh = false)

        syncDataSource.upNextNitroInvocations() shouldBe 1
        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should fetch given activity changed`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))
        requestManager.requestValid = true
        activityRepository.setActivityChanged(ActivityType.EPISODES_WATCHED, changed = true)

        store.fetchWith(key = ContinueWatchingKey.Nitro, forceRefresh = false)

        syncDataSource.upNextNitroInvocations() shouldBe 1
        continueWatchingDao.entries() shouldContainExactlyInAnyOrder listOf(breakingBadEntry)
    }

    @Test
    fun `should leave dao unchanged given fetcher signals skip`() = runTest(testDispatcher) {
        continueWatchingDao.upsert(breakingBadEntry)
        activityRepository.setEpisodesWatchedSyncTimeStamp(NOW - kotlin.time.Duration.parse("1h"))
        syncDataSource.setUpNextNitro(ApiResponse.Success(emptyList()))
        requestManager.requestValid = false

        try {
            store.fetchWith(key = ContinueWatchingKey.Nitro, forceRefresh = false)
        } catch (_: FetcherSkipSignal) {
            // expected: empty Nitro + fresh cursor + no force refresh = guard trips
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

private val breakingBadPlayback = TraktPlaybackEpisodeResponse(
    id = 100001,
    progress = 45.0,
    pausedAt = "2026-05-10T20:15:00.000Z",
    type = "episode",
    episode = TraktNextEpisodeResponse(
        seasonNumber = 4,
        episodeNumber = 1,
        ids = EpisodeIds(trakt = 401, tmdb = null),
    ),
    show = TraktShowResponse(
        title = "Breaking Bad",
        ids = ShowIds(trakt = BREAKING_BAD_ID, slug = "breaking-bad", tmdb = 1396),
        airedEpisodes = 62,
    ),
)

private val breakingBadProgress = TraktWatchedProgressResponse(
    aired = 62,
    completed = 30,
    lastWatchedAt = "2026-05-10T20:15:00Z",
    nextEpisode = TraktNextEpisodeResponse(
        seasonNumber = 4,
        episodeNumber = 1,
        ids = EpisodeIds(trakt = 401, tmdb = null),
    ),
)

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
    traktId = BREAKING_BAD_ID,
    tmdbId = 1396,
    airedEpisodes = 62,
    completedCount = 30,
    lastWatchedAt = Instant.parse("2026-05-10T20:15:00Z").toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse("2026-05-10T20:15:00Z").toEpochMilliseconds(),
    title = "Breaking Bad",
)

private val theWireEntry = ContinueWatchingEntry(
    traktId = THE_WIRE_ID,
    tmdbId = 1438,
    airedEpisodes = 60,
    completedCount = 12,
    lastWatchedAt = Instant.parse("2026-04-22T09:00:00Z").toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse("2026-04-22T09:00:00Z").toEpochMilliseconds(),
    title = "The Wire",
)
