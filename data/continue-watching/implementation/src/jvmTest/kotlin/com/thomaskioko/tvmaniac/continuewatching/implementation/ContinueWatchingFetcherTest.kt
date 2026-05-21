package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.shows.testing.FakeTvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.model.EpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHiddenItemResponse
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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class ContinueWatchingFetcherTest {

    private val testDispatcher = StandardTestDispatcher()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val syncDataSource = FakeTraktSyncRemoteDataSource()
    private val userDataSource = FakeTraktUserRemoteDataSource()
    private val activityRepository = FakeTraktActivityRepository()
    private val continueWatchingDao = FakeContinueWatchingDao()
    private val tvShowsDao = FakeTvShowsDao()
    private val requestManager = FakeRequestManagerRepository()
    private val transactionRunner = object : DatabaseTransactionRunner {
        override fun <T> invoke(block: () -> T): T = block()
    }
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val dateTimeProvider = FakeDateTimeProvider(currentTime = NOW)
    private val logger = FakeLogger()

    private lateinit var nitroFetcher: NitroContinueWatchingFetcher
    private lateinit var discoveryStore: ContinueWatchingDiscoveryStore
    private lateinit var store: ContinueWatchingStore

    @BeforeTest
    fun setUp() {
        seedBaselineFixtures()
        nitroFetcher = NitroContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
            dateTimeProvider = dateTimeProvider,
            logger = logger,
        )
        discoveryStore = ContinueWatchingDiscoveryStore(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            continueWatchingDao = continueWatchingDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = requestManager,
            traktActivityRepository = activityRepository,
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
        )
        store = ContinueWatchingStore(
            nitroFetcher = nitroFetcher,
            traktSyncDataSource = syncDataSource,
            continueWatchingDao = continueWatchingDao,
            tvShowsDao = tvShowsDao,
            requestManagerRepository = requestManager,
            traktActivityRepository = activityRepository,
            datastoreRepository = FakeDatastoreRepository(),
            transactionRunner = transactionRunner,
            dispatchers = dispatchers,
            logger = logger,
        )
    }

    @Test
    fun `should produce identical entry sets across progress and nitro paths`() = runTest(testDispatcher) {
        val progressEntries = syncProgressAndReadDao(forceRefresh = false)
        continueWatchingDao.deleteAll()
        val nitroResult = nitroFetcher.run(forceRefresh = false).shouldNotBeNull()

        progressEntries.map { it.parityTuple() } shouldContainExactlyInAnyOrder
            listOf(breakingBadTuple, theWireTuple)
        nitroResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder
            progressEntries.map { it.parityTuple() }
    }

    @Test
    fun `should produce identical empty sets given no in-progress shows`() = runTest(testDispatcher) {
        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(emptyList()))
        userDataSource.setHiddenProgressWatched(ApiResponse.Success(emptyList()))
        syncDataSource.setUpNextNitro(ApiResponse.Success(emptyList()))
        syncDataSource.setWatchedShows(ApiResponse.Success(emptyList()))
        continueWatchingDao.deleteAll()
        // Stale cursor so Nitro's empty-response guard does not engage.
        activityRepository.setEpisodesWatchedSyncTimeStamp(NOW - 7.hours)

        val progressEntries = syncProgressAndReadDao(forceRefresh = false)
        continueWatchingDao.deleteAll()
        val nitroResult = nitroFetcher.run(forceRefresh = false).shouldNotBeNull()

        progressEntries.shouldBeEmpty()
        nitroResult.shouldBeEmpty()
    }

    @Test
    fun `should filter hidden shows from both paths even when nitro fails to pre-filter`() =
        runTest(testDispatcher) {
            // Trakt sometimes returns hidden shows in the Nitro feed (server bug). The
            // client-side filter in NitroContinueWatchingFetcher must catch this, keeping
            // parity with discovery's pre-filter that drops hidden shows before per-show fetch.
            val baselineNitro: List<TraktUpNextNitroResponse> =
                json.decodeFromString(load("nitro_up_next.json"))
            val baselinePlayback: List<TraktPlaybackEpisodeResponse> =
                json.decodeFromString(load("playback_episodes.json"))
            syncDataSource.setUpNextNitro(
                ApiResponse.Success(baselineNitro + darkNitroResponse),
            )
            syncDataSource.setPlaybackEpisodes(
                ApiResponse.Success(baselinePlayback + darkPlaybackResponse),
            )

            val progressEntries = syncProgressAndReadDao(forceRefresh = false)
            continueWatchingDao.deleteAll()
            val nitroResult = nitroFetcher.run(forceRefresh = false).shouldNotBeNull()

            val expected = listOf(breakingBadTuple, theWireTuple)
            progressEntries.map { it.parityTuple() } shouldContainExactlyInAnyOrder expected
            nitroResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder expected
        }

    @Test
    fun `should produce identical entry sets given forceRefresh is true`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(NOW - 1.hours)

        val progressEntries = syncProgressAndReadDao(forceRefresh = true)
        continueWatchingDao.deleteAll()
        val nitroResult = nitroFetcher.run(forceRefresh = true).shouldNotBeNull()

        progressEntries.map { it.parityTuple() } shouldContainExactlyInAnyOrder
            listOf(breakingBadTuple, theWireTuple)
        nitroResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder
            progressEntries.map { it.parityTuple() }

        // Sanity check the internal behavior difference: the Progress fan-out passes
        // lastActivity=null when forceRefresh is true; the Nitro fetcher skips the empty-
        // response guard. Both still write through to the same mapped entries.
        syncDataSource.showWatchedProgressLastActivity(BREAKING_BAD_ID) shouldBe null
    }

    @Test
    fun `should deserialize and map a real Nitro response captured from production`() =
        runTest(testDispatcher) {
            // Captured 2026-05-20 from a live Trakt account. Pinned here so future Nitro
            // shape changes (new top-level fields, renamed progress fields, etc.) surface
            // as a deserialization failure rather than silent data loss.
            //
            // The real response carries extra fields beyond TraktUpNextNitroResponse
            // (top-level `show_id`, `cached_aired_episode_count`, `total_count`;
            // `progress.stats` and `progress.hidden`). `ignoreUnknownKeys = true` in
            // the production Json config (TraktBindingContainer) drops them silently.
            val realNitro: List<TraktUpNextNitroResponse> =
                json.decodeFromString(load("nitro_up_next_real.json"))
            syncDataSource.setUpNextNitro(ApiResponse.Success(realNitro))
            userDataSource.setHiddenProgressWatched(ApiResponse.Success(emptyList()))

            val result = nitroFetcher.run(forceRefresh = false).shouldNotBeNull()

            result.size shouldBe 1
            val entry = result.single()
            entry.traktId shouldBe HELLS_PARADISE_ID
            entry.tmdbId shouldBe 117465L
            entry.airedEpisodes shouldBe 25L
            entry.completedCount shouldBe 20L
            entry.lastWatchedAt shouldBe Instant.parse("2026-05-19T16:28:00Z").toEpochMilliseconds()
        }

    @Test
    fun `should filter reset show with null next episode from both paths`() = runTest(testDispatcher) {
        // A reset show appears in playback feed but Trakt's per-show progress returns
        // null next_episode (reset_at populated). Both paths must drop the row;
        // otherwise we persist an entry that has no episode to surface.
        val baselinePlayback: List<TraktPlaybackEpisodeResponse> =
            json.decodeFromString(load("playback_episodes.json"))
        val baselineNitro: List<TraktUpNextNitroResponse> =
            json.decodeFromString(load("nitro_up_next.json"))

        syncDataSource.setPlaybackEpisodes(
            ApiResponse.Success(baselinePlayback + resetShowPlayback),
        )
        syncDataSource.setShowWatchedProgress(
            traktId = RESET_SHOW_ID,
            response = ApiResponse.Success(resetShowProgress),
        )
        syncDataSource.setUpNextNitro(
            ApiResponse.Success(baselineNitro + resetShowNitro),
        )

        val progressEntries = syncProgressAndReadDao(forceRefresh = false)
        continueWatchingDao.deleteAll()
        val nitroResult = nitroFetcher.run(forceRefresh = false).shouldNotBeNull()

        val expected = listOf(breakingBadTuple, theWireTuple)
        progressEntries.map { it.parityTuple() } shouldContainExactlyInAnyOrder expected
        nitroResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder expected

        // Sanity check: the Progress fan-out called per-show progress for the reset show
        // (it was a candidate via playback), then dropped the row because nextEpisode was null.
        // If the filter regressed, RESET_SHOW_ID would appear in the result.
        syncDataSource.showWatchedProgressInvocations(RESET_SHOW_ID) shouldBe 1
    }

    private suspend fun syncProgressAndReadDao(forceRefresh: Boolean): List<ContinueWatchingEntry> {
        requestManager.requestValid = false
        discoveryStore.fetchWith(forceRefresh)
        runCatching { store.fetchWith(ContinueWatchingKey.Progress, forceRefresh) }
            .onFailure { error -> if (error !is FetcherSkipSignal) throw error }
        return continueWatchingDao.entries()
    }

    private fun seedBaselineFixtures() {
        val playback: List<TraktPlaybackEpisodeResponse> = json.decodeFromString(load("playback_episodes.json"))
        val hidden: List<TraktHiddenItemResponse> = json.decodeFromString(load("hidden_progress.json"))
        val nitro: List<TraktUpNextNitroResponse> = json.decodeFromString(load("nitro_up_next.json"))

        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(playback))
        userDataSource.setHiddenProgressWatched(ApiResponse.Success(hidden))
        syncDataSource.setUpNextNitro(ApiResponse.Success(nitro))

        syncDataSource.setShowWatchedProgress(
            traktId = BREAKING_BAD_ID,
            response = ApiResponse.Success(loadProgress("progress_1388.json")),
        )
        syncDataSource.setShowWatchedProgress(
            traktId = THE_WIRE_ID,
            response = ApiResponse.Success(loadProgress("progress_1429.json")),
        )
    }

    private fun loadProgress(name: String): TraktWatchedProgressResponse =
        json.decodeFromString(load(name))

    private fun load(name: String): String =
        Thread.currentThread().contextClassLoader!!.getResource(name)!!.readText()
}

private val NOW: Instant = Instant.parse("2026-05-20T12:00:00Z")
private const val BREAKING_BAD_ID = 1388L
private const val THE_WIRE_ID = 1429L
private const val DARK_ID = 444L
private const val RESET_SHOW_ID = 5000L
private const val HELLS_PARADISE_ID = 181120L

private data class ParityTuple(
    val traktId: Long,
    val completedCount: Long,
    val airedEpisodes: Long,
    val lastWatchedAt: Long,
)

private fun ContinueWatchingEntry.parityTuple(): ParityTuple = ParityTuple(
    traktId = traktId,
    completedCount = completedCount,
    airedEpisodes = airedEpisodes,
    lastWatchedAt = lastWatchedAt,
)

private val breakingBadTuple = ParityTuple(
    traktId = BREAKING_BAD_ID,
    completedCount = 30,
    airedEpisodes = 62,
    lastWatchedAt = Instant.parse("2026-05-10T20:15:00Z").toEpochMilliseconds(),
)

private val theWireTuple = ParityTuple(
    traktId = THE_WIRE_ID,
    completedCount = 12,
    airedEpisodes = 60,
    lastWatchedAt = Instant.parse("2026-04-22T09:00:00Z").toEpochMilliseconds(),
)

private val darkNitroResponse = TraktUpNextNitroResponse(
    show = TraktShowResponse(
        title = "Dark",
        year = 2017,
        ids = ShowIds(trakt = DARK_ID, slug = "dark", tmdb = 70523),
        airedEpisodes = 26,
    ),
    progress = TraktWatchedProgressResponse(
        aired = 26,
        completed = 15,
        lastWatchedAt = "2025-12-01T10:00:00Z",
        nextEpisode = TraktNextEpisodeResponse(
            seasonNumber = 2,
            episodeNumber = 5,
            ids = EpisodeIds(trakt = 205, tmdb = null),
        ),
    ),
)

private val darkPlaybackResponse = TraktPlaybackEpisodeResponse(
    id = 100099,
    progress = 30.0,
    pausedAt = "2025-12-01T10:00:00.000Z",
    type = "episode",
    episode = TraktNextEpisodeResponse(
        seasonNumber = 2,
        episodeNumber = 5,
        ids = EpisodeIds(trakt = 205, tmdb = null),
    ),
    show = TraktShowResponse(
        title = "Dark",
        year = 2017,
        ids = ShowIds(trakt = DARK_ID, slug = "dark", tmdb = 70523),
        airedEpisodes = 26,
    ),
)

private val resetShowPlayback = TraktPlaybackEpisodeResponse(
    id = 100500,
    progress = 10.0,
    pausedAt = "2025-09-10T14:00:00.000Z",
    type = "episode",
    episode = TraktNextEpisodeResponse(
        seasonNumber = 1,
        episodeNumber = 1,
        ids = EpisodeIds(trakt = 5001, tmdb = null),
    ),
    show = TraktShowResponse(
        title = "Reset Show",
        year = 2024,
        ids = ShowIds(trakt = RESET_SHOW_ID, slug = "reset-show", tmdb = 99999),
        airedEpisodes = 20,
    ),
)

private val resetShowProgress = TraktWatchedProgressResponse(
    aired = 20,
    completed = 0,
    lastWatchedAt = "2025-09-10T14:00:00Z",
    resetAt = "2026-01-01T00:00:00Z",
    nextEpisode = null,
)

private val resetShowNitro = TraktUpNextNitroResponse(
    show = TraktShowResponse(
        title = "Reset Show",
        year = 2024,
        ids = ShowIds(trakt = RESET_SHOW_ID, slug = "reset-show", tmdb = 99999),
        airedEpisodes = 20,
    ),
    progress = TraktWatchedProgressResponse(
        aired = 20,
        completed = 0,
        lastWatchedAt = "2025-09-10T14:00:00Z",
        resetAt = "2026-01-01T00:00:00Z",
        nextEpisode = null,
    ),
)
