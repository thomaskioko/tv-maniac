package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.model.EpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHiddenItemResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktNextEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedProgressResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktSyncRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktUserRemoteDataSource
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
internal class ProgressContinueWatchingFetcherTest {

    private val testDispatcher = StandardTestDispatcher()
    private val syncDataSource = FakeTraktSyncRemoteDataSource()
    private val userDataSource = FakeTraktUserRemoteDataSource()
    private val activityRepository = FakeTraktActivityRepository()

    private lateinit var fetcher: ProgressContinueWatchingFetcher

    @BeforeTest
    fun setUp() {
        fetcher = ProgressContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
        )
    }

    @Test
    fun `should exclude hidden shows from candidate set`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(
            ApiResponse.Success(listOf(breakingBadInProgress, theWireInProgress)),
        )
        userDataSource.setHiddenProgressWatched(
            ApiResponse.Success(listOf(hiddenItem(traktId = BREAKING_BAD_ID))),
        )
        syncDataSource.setShowWatchedProgress(THE_WIRE_ID, ApiResponse.Success(theWireProgress))

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull().map { it.traktId } shouldContainExactlyInAnyOrder listOf(THE_WIRE_ID)
        syncDataSource.showWatchedProgressInvocations(BREAKING_BAD_ID) shouldBe 0
        syncDataSource.showWatchedProgressInvocations(THE_WIRE_ID) shouldBe 1
    }

    @Test
    fun `should pass cursor to per-show progress calls given force refresh is false`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(Instant.parse(CURSOR_ISO))
        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(breakingBadInProgress)))
        syncDataSource.setShowWatchedProgress(
            BREAKING_BAD_ID,
            ApiResponse.Success(breakingBadProgress),
        )

        fetcher.run(forceRefresh = false)

        syncDataSource.showWatchedProgressLastActivity(BREAKING_BAD_ID) shouldBe CURSOR_ISO
    }

    @Test
    fun `should pass null cursor given force refresh is true`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(Instant.parse(CURSOR_ISO))
        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(breakingBadInProgress)))
        syncDataSource.setShowWatchedProgress(
            BREAKING_BAD_ID,
            ApiResponse.Success(breakingBadProgress),
        )

        fetcher.run(forceRefresh = true)

        syncDataSource.showWatchedProgressLastActivity(BREAKING_BAD_ID) shouldBe null
    }

    @Test
    fun `should filter rows where next episode is null even when plays is less than aired`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(resetShow)))
        syncDataSource.setShowWatchedProgress(
            RESET_SHOW_ID,
            ApiResponse.Success(resetShowProgress),
        )

        val result = fetcher.run(forceRefresh = false)

        result.shouldBeEmpty()
    }

    @Test
    fun `should exclude rows where show has null aired episodes`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(ApiResponse.Success(listOf(showWithoutAiredCount)))

        val result = fetcher.run(forceRefresh = false)

        result.shouldBeEmpty()
        syncDataSource.showWatchedProgressInvocations(UNKNOWN_AIRED_ID) shouldBe 0
    }

    @Test
    fun `should return entries given populated watched and empty hidden`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(
            ApiResponse.Success(listOf(breakingBadInProgress, theWireInProgress)),
        )
        syncDataSource.setShowWatchedProgress(
            BREAKING_BAD_ID,
            ApiResponse.Success(breakingBadProgress),
        )
        syncDataSource.setShowWatchedProgress(THE_WIRE_ID, ApiResponse.Success(theWireProgress))

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull() shouldContainExactlyInAnyOrder listOf(
            breakingBadEntry,
            theWireEntry,
        )
    }

    @Test
    fun `should signal skip given watched shows call fails`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(ApiResponse.Error.HttpError(code = 500, errorBody = "boom", errorMessage = "boom"))

        val result = fetcher.run(forceRefresh = false)

        result shouldBe null
    }

    @Test
    fun `should skip rows where per-show progress call fails`() = runTest(testDispatcher) {
        syncDataSource.setWatchedShows(
            ApiResponse.Success(listOf(breakingBadInProgress, theWireInProgress)),
        )
        syncDataSource.setShowWatchedProgress(
            BREAKING_BAD_ID,
            ApiResponse.Success(breakingBadProgress),
        )
        syncDataSource.setShowWatchedProgress(THE_WIRE_ID, ApiResponse.Error.HttpError(code = 500, errorBody = "boom", errorMessage = "boom"))

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull().map { it.traktId } shouldContainExactlyInAnyOrder listOf(BREAKING_BAD_ID)
    }
}

private const val BREAKING_BAD_ID = 1388L
private const val THE_WIRE_ID = 1429L
private const val RESET_SHOW_ID = 2000L
private const val UNKNOWN_AIRED_ID = 3000L
private const val CURSOR_ISO = "2026-05-19T08:30:00Z"

private val breakingBadInProgress = TraktWatchedShowResponse(
    plays = 30,
    lastWatchedAt = "2026-05-10T20:15:00Z",
    lastUpdatedAt = "2026-05-10T20:15:00Z",
    show = TraktShowResponse(
        title = "Breaking Bad",
        ids = ShowIds(trakt = BREAKING_BAD_ID, slug = "breaking-bad", tmdb = 1396),
        airedEpisodes = 62,
    ),
)

private val theWireInProgress = TraktWatchedShowResponse(
    plays = 12,
    lastWatchedAt = "2026-04-22T09:00:00Z",
    lastUpdatedAt = "2026-04-22T09:00:00Z",
    show = TraktShowResponse(
        title = "The Wire",
        ids = ShowIds(trakt = THE_WIRE_ID, slug = "the-wire", tmdb = 1438),
        airedEpisodes = 60,
    ),
)

private val resetShow = TraktWatchedShowResponse(
    plays = 5,
    lastWatchedAt = "2026-05-15T18:00:00Z",
    lastUpdatedAt = "2026-05-15T18:00:00Z",
    show = TraktShowResponse(
        title = "Some Reset Show",
        ids = ShowIds(trakt = RESET_SHOW_ID, slug = "some-reset-show", tmdb = 9000),
        airedEpisodes = 20,
    ),
)

private val showWithoutAiredCount = TraktWatchedShowResponse(
    plays = 1,
    lastWatchedAt = "2026-05-01T10:00:00Z",
    lastUpdatedAt = "2026-05-01T10:00:00Z",
    show = TraktShowResponse(
        title = "Unknown Aired",
        ids = ShowIds(trakt = UNKNOWN_AIRED_ID, slug = "unknown-aired", tmdb = 9100),
        airedEpisodes = null,
    ),
)

private val breakingBadProgress = TraktWatchedProgressResponse(
    aired = 62,
    completed = 30,
    nextEpisode = nextEpisode(seasonNumber = 4, episodeNumber = 1),
)

private val theWireProgress = TraktWatchedProgressResponse(
    aired = 60,
    completed = 12,
    nextEpisode = nextEpisode(seasonNumber = 1, episodeNumber = 13),
)

private val resetShowProgress = TraktWatchedProgressResponse(
    aired = 20,
    completed = 0,
    resetAt = "2026-05-15T18:00:00Z",
    nextEpisode = null,
)

private val breakingBadEntry = ContinueWatchingEntry(
    traktId = BREAKING_BAD_ID,
    tmdbId = 1396,
    airedEpisodes = 62,
    completedCount = 30,
    lastWatchedAt = Instant.parse("2026-05-10T20:15:00Z").toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse("2026-05-10T20:15:00Z").toEpochMilliseconds(),
)

private val theWireEntry = ContinueWatchingEntry(
    traktId = THE_WIRE_ID,
    tmdbId = 1438,
    airedEpisodes = 60,
    completedCount = 12,
    lastWatchedAt = Instant.parse("2026-04-22T09:00:00Z").toEpochMilliseconds(),
    lastUpdatedAt = Instant.parse("2026-04-22T09:00:00Z").toEpochMilliseconds(),
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
