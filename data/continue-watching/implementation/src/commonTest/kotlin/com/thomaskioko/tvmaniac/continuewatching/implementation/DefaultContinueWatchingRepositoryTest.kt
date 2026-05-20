package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeTvShowsDao
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
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultContinueWatchingRepositoryTest {

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
    private val activityRepository = FakeTraktActivityRepository()
    private val requestManager = FakeRequestManagerRepository()
    private val continueWatchingDao = FakeContinueWatchingDao()
    private val tvShowsDao = FakeTvShowsDao()
    private val dateTimeProvider = FakeDateTimeProvider(currentTime = NOW)

    private lateinit var repository: DefaultContinueWatchingRepository

    @BeforeTest
    fun setUp() {
        val transactionRunner = object : DatabaseTransactionRunner {
            override fun <T> invoke(block: () -> T): T = block()
        }
        val progressFetcher = ProgressContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
            continueWatchingDao = continueWatchingDao,
            tvShowsDao = tvShowsDao,
            transactionRunner = transactionRunner,
        )
        val nitroFetcher = NitroContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
            dateTimeProvider = dateTimeProvider,
            logger = FakeLogger(),
        )
        val store = ContinueWatchingStore(
            progressFetcher = progressFetcher,
            nitroFetcher = nitroFetcher,
            continueWatchingDao = continueWatchingDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = requestManager,
            traktActivityRepository = activityRepository,
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
        )
        repository = DefaultContinueWatchingRepository(
            continueWatchingStore = store,
        )
    }

    @Test
    fun `should route to progress fetcher given useNitro false`() = runTest(testDispatcher) {
        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(listOf(breakingBadPlayback)))
        syncDataSource.setShowWatchedProgress(BREAKING_BAD_ID, ApiResponse.Success(breakingBadProgress))

        repository.sync(forceRefresh = true, useNitro = false)

        syncDataSource.playbackEpisodesInvocations() shouldBe 1
        syncDataSource.upNextNitroInvocations() shouldBe 0
    }

    @Test
    fun `should route to nitro fetcher given useNitro true`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))

        repository.sync(forceRefresh = true, useNitro = true)

        syncDataSource.upNextNitroInvocations() shouldBe 1
        syncDataSource.playbackEpisodesInvocations() shouldBe 0
    }

    @Test
    fun `should swallow fetcher skip signal silently`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(NOW)
        syncDataSource.setUpNextNitro(ApiResponse.Success(emptyList()))
        // Force the validator stale so the fetcher actually runs; otherwise the
        // signal never fires and the swallow path is never exercised.
        requestManager.requestValid = false

        // empty Nitro + fresh cursor + no force = guard trips, fetcher returns null,
        // store throws FetcherSkipSignal which the repo must swallow.
        repository.sync(forceRefresh = false, useNitro = true)

        // Sanity: the Nitro endpoint WAS called (proving the validator did not
        // short-circuit the test).
        syncDataSource.upNextNitroInvocations() shouldBe 1
    }

    @Test
    fun `should pick fetcher per call without caching previous choice`() = runTest(testDispatcher) {
        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(listOf(breakingBadPlayback)))
        syncDataSource.setShowWatchedProgress(BREAKING_BAD_ID, ApiResponse.Success(breakingBadProgress))
        syncDataSource.setUpNextNitro(ApiResponse.Success(listOf(breakingBadNitro)))

        repository.sync(forceRefresh = true, useNitro = false)

        syncDataSource.playbackEpisodesInvocations() shouldBe 1
        syncDataSource.upNextNitroInvocations() shouldBe 0

        repository.sync(forceRefresh = true, useNitro = true)

        syncDataSource.upNextNitroInvocations() shouldBe 1
    }
}

private val NOW: Instant = Instant.parse("2026-05-20T12:00:00Z")
private const val BREAKING_BAD_ID = 1388L

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
