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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class NitroContinueWatchingFetcherTest {

    private val testDispatcher = StandardTestDispatcher()
    private val syncDataSource = FakeTraktSyncRemoteDataSource()
    private val userDataSource = FakeTraktUserRemoteDataSource()
    private val activityRepository = FakeTraktActivityRepository()
    private val dateTimeProvider = FakeDateTimeProvider(currentTime = NOW)
    private val logger = FakeLogger()

    private lateinit var fetcher: NitroContinueWatchingFetcher

    @BeforeTest
    fun setUp() {
        fetcher = NitroContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
            dateTimeProvider = dateTimeProvider,
            logger = logger,
        )
    }

    @Test
    fun `should map populated nitro response to entries`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(
            ApiResponse.Success(listOf(breakingBadNitro, theWireNitro)),
        )

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull() shouldContainExactlyInAnyOrder listOf(
            breakingBadEntry,
            theWireEntry,
        )
    }

    @Test
    fun `should filter out hidden shows`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(
            ApiResponse.Success(listOf(breakingBadNitro, theWireNitro)),
        )
        userDataSource.setHiddenProgressWatched(
            ApiResponse.Success(listOf(hiddenItem(traktId = BREAKING_BAD_ID))),
        )

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull().map { it.traktId } shouldContainExactlyInAnyOrder listOf(THE_WIRE_ID)
    }

    @Test
    fun `should filter out rows with null next episode`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(
            ApiResponse.Success(listOf(breakingBadNitro, resetShowNitro)),
        )

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull().map { it.traktId } shouldContainExactlyInAnyOrder listOf(BREAKING_BAD_ID)
    }

    @Test
    fun `should return null given empty response and fresh cursor and no force refresh`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(NOW - 1.hours)
        syncDataSource.setUpNextNitro(ApiResponse.Success(emptyList()))

        val result = fetcher.run(forceRefresh = false)

        result shouldBe null
    }

    @Test
    fun `should write through empty response given force refresh`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(NOW - 1.hours)
        syncDataSource.setUpNextNitro(ApiResponse.Success(emptyList()))

        val result = fetcher.run(forceRefresh = true)

        result.shouldNotBeNull().shouldBeEmpty()
    }

    @Test
    fun `should write through empty response given stale cursor`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(NOW - 7.hours)
        syncDataSource.setUpNextNitro(ApiResponse.Success(emptyList()))

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull().shouldBeEmpty()
    }

    @Test
    fun `should write through empty response given no cursor at all`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(null)
        syncDataSource.setUpNextNitro(ApiResponse.Success(emptyList()))

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull().shouldBeEmpty()
    }

    @Test
    fun `should return null given nitro http error`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(
            ApiResponse.Error.HttpError(code = 500, errorBody = "boom", errorMessage = "boom"),
        )

        val result = fetcher.run(forceRefresh = false)

        result shouldBe null
    }

    @Test
    fun `should signal skip given hidden call fails`() = runTest(testDispatcher) {
        syncDataSource.setUpNextNitro(
            ApiResponse.Success(listOf(breakingBadNitro)),
        )
        userDataSource.setHiddenProgressWatched(
            ApiResponse.Error.HttpError(code = 500, errorBody = "boom", errorMessage = "boom"),
        )

        val result = fetcher.run(forceRefresh = false)

        result shouldBe null
    }
}

private val NOW: Instant = Instant.parse("2026-05-20T12:00:00Z")
private const val BREAKING_BAD_ID = 1388L
private const val THE_WIRE_ID = 1429L
private const val RESET_SHOW_ID = 2000L

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
        nextEpisode = nextEpisode(seasonNumber = 4, episodeNumber = 1),
    ),
)

private val theWireNitro = TraktUpNextNitroResponse(
    show = TraktShowResponse(
        title = "The Wire",
        ids = ShowIds(trakt = THE_WIRE_ID, slug = "the-wire", tmdb = 1438),
        airedEpisodes = 60,
    ),
    progress = TraktWatchedProgressResponse(
        aired = 60,
        completed = 12,
        lastWatchedAt = "2026-04-22T09:00:00Z",
        nextEpisode = nextEpisode(seasonNumber = 1, episodeNumber = 13),
    ),
)

private val resetShowNitro = TraktUpNextNitroResponse(
    show = TraktShowResponse(
        title = "Reset Show",
        ids = ShowIds(trakt = RESET_SHOW_ID, slug = "reset-show", tmdb = 9000),
        airedEpisodes = 20,
    ),
    progress = TraktWatchedProgressResponse(
        aired = 20,
        completed = 0,
        resetAt = "2026-05-15T18:00:00Z",
        nextEpisode = null,
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

private fun hiddenItem(traktId: Long): TraktHiddenItemResponse =
    TraktHiddenItemResponse(
        hiddenAt = "2026-04-01T12:00:00Z",
        type = "show",
        show = TraktShowResponse(
            title = "Hidden",
            ids = ShowIds(trakt = traktId, slug = "hidden-$traktId"),
        ),
    )

private fun nextEpisode(seasonNumber: Int, episodeNumber: Int): TraktNextEpisodeResponse =
    TraktNextEpisodeResponse(
        seasonNumber = seasonNumber,
        episodeNumber = episodeNumber,
        ids = EpisodeIds(trakt = seasonNumber * 100 + episodeNumber, tmdb = null),
    )
