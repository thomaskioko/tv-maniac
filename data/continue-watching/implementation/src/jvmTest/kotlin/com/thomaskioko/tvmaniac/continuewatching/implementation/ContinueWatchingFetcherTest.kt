package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.model.EpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHiddenItemResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktNextEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktUpNextNitroResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
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
    private val dateTimeProvider = FakeDateTimeProvider(currentTime = NOW)
    private val logger = FakeLogger()

    private lateinit var progressFetcher: ProgressContinueWatchingFetcher
    private lateinit var nitroFetcher: NitroContinueWatchingFetcher

    @BeforeTest
    fun setUp() {
        seedBaselineFixtures()
        progressFetcher = ProgressContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
        )
        nitroFetcher = NitroContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
            dateTimeProvider = dateTimeProvider,
            logger = logger,
        )
    }

    @Test
    fun `should produce identical entry sets from both fetchers given the same logical state`() =
        runTest(testDispatcher) {
            val progressResult = progressFetcher.run(forceRefresh = false).shouldNotBeNull()
            val nitroResult = nitroFetcher.run(forceRefresh = false).shouldNotBeNull()

            val progressTuples = progressResult.map { it.parityTuple() }
            val nitroTuples = nitroResult.map { it.parityTuple() }

            progressTuples shouldContainExactlyInAnyOrder listOf(breakingBadTuple, theWireTuple)
            nitroTuples shouldContainExactlyInAnyOrder progressTuples
        }

    @Test
    fun `should produce identical empty sets given no in-progress shows`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(ApiResponse.Success(emptyList()))
        userDataSource.setHiddenProgressWatched(ApiResponse.Success(emptyList()))
        syncDataSource.setUpNextNitro(ApiResponse.Success(emptyList()))
        // Stale cursor so Nitro's empty-response guard does not engage.
        activityRepository.setEpisodesWatchedSyncTimeStamp(NOW - 7.hours)

        val progressResult = progressFetcher.run(forceRefresh = false).shouldNotBeNull()
        val nitroResult = nitroFetcher.run(forceRefresh = false).shouldNotBeNull()

        progressResult.shouldBeEmpty()
        nitroResult.shouldBeEmpty()
    }

    @Test
    fun `should filter hidden shows from both fetchers even when nitro fails to pre-filter`() =
        runTest(testDispatcher) {
            // Trakt sometimes returns hidden shows in the Nitro feed (server bug). The
            // client-side filter in NitroContinueWatchingFetcher must catch this, keeping
            // parity with Progress's pre-filter that drops hidden shows before per-show fetch.
            val baselineNitro: List<TraktUpNextNitroResponse> =
                json.decodeFromString(load("nitro_up_next.json"))
            syncDataSource.setUpNextNitro(
                ApiResponse.Success(baselineNitro + darkNitroResponse),
            )

            val progressResult = progressFetcher.run(forceRefresh = false).shouldNotBeNull()
            val nitroResult = nitroFetcher.run(forceRefresh = false).shouldNotBeNull()

            val expected = listOf(breakingBadTuple, theWireTuple)
            progressResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder expected
            nitroResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder expected
        }

    @Test
    fun `should produce identical entry sets given forceRefresh is true`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(NOW - 1.hours)

        val progressResult = progressFetcher.run(forceRefresh = true).shouldNotBeNull()
        val nitroResult = nitroFetcher.run(forceRefresh = true).shouldNotBeNull()

        progressResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder
            listOf(breakingBadTuple, theWireTuple)
        nitroResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder
            progressResult.map { it.parityTuple() }

        // Sanity check the internal behavior difference: Progress passes lastActivity=null
        // when forceRefresh is true, Nitro skips the guard. Both still write through to the
        // same mapped entries.
        syncDataSource.showWatchedProgressLastActivity(BREAKING_BAD_ID) shouldBe null
    }

    @Test
    fun `should deserialize and map a real Nitro response captured from production`() =
        runTest(testDispatcher) {
            // Captured 2026-05-20 from a live Trakt account. Pinned here so future Nitro
            // shape changes (new top-level fields, renamed progress fields, etc.) surface
            // as a deserialization failure rather than silent data loss. See PRD Phase 0.
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
    fun `should filter reset show with null next episode from both fetchers`() = runTest(testDispatcher) {
        // A reset show passes the `plays < aired_episodes` candidate filter but Trakt's
        // per-show progress returns null next_episode (reset_at populated). Both fetchers
        // must drop the row; otherwise we persist an entry that has no episode to surface.
        val baselineWatched: List<TraktWatchedShowResponse> =
            json.decodeFromString(load("watched_shows_full.json"))
        val baselineNitro: List<TraktUpNextNitroResponse> =
            json.decodeFromString(load("nitro_up_next.json"))

        syncDataSource.setWatchedShows(
            ApiResponse.Success(baselineWatched + resetShowWatched),
        )
        syncDataSource.setShowWatchedProgress(
            traktId = RESET_SHOW_ID,
            response = ApiResponse.Success(resetShowProgress),
        )
        syncDataSource.setUpNextNitro(
            ApiResponse.Success(baselineNitro + resetShowNitro),
        )

        val progressResult = progressFetcher.run(forceRefresh = false).shouldNotBeNull()
        val nitroResult = nitroFetcher.run(forceRefresh = false).shouldNotBeNull()

        val expected = listOf(breakingBadTuple, theWireTuple)
        progressResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder expected
        nitroResult.map { it.parityTuple() } shouldContainExactlyInAnyOrder expected

        // Sanity check: Pipeline A made the per-show progress call (passed candidate filter),
        // then dropped the row because nextEpisode was null. If the filter regressed, we would
        // see RESET_SHOW_ID in the result.
        syncDataSource.showWatchedProgressInvocations(RESET_SHOW_ID) shouldBe 1
    }

    private fun seedBaselineFixtures() {
        val watched: List<TraktWatchedShowResponse> = json.decodeFromString(load("watched_shows_full.json"))
        val hidden: List<TraktHiddenItemResponse> = json.decodeFromString(load("hidden_progress.json"))
        val nitro: List<TraktUpNextNitroResponse> = json.decodeFromString(load("nitro_up_next.json"))

        syncDataSource.setWatchedShows(ApiResponse.Success(watched))
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

private val resetShowWatched = TraktWatchedShowResponse(
    plays = 8,
    lastWatchedAt = "2025-09-10T14:00:00Z",
    lastUpdatedAt = "2025-09-10T14:00:00Z",
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
