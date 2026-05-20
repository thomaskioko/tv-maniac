package com.thomaskioko.tvmaniac.continuewatching.implementation

import com.thomaskioko.tvmaniac.continuewatching.api.ContinueWatchingEntry
import com.thomaskioko.tvmaniac.continuewatching.testing.FakeContinueWatchingDao
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.DatabaseTransactionRunner
import com.thomaskioko.tvmaniac.shows.testing.FakeTvShowsDao
import com.thomaskioko.tvmaniac.syncactivity.testing.FakeTraktActivityRepository
import com.thomaskioko.tvmaniac.trakt.api.model.EpisodeIds
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktHiddenItemResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktNextEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktPlaybackEpisodeResponse
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
    private val continueWatchingDao = FakeContinueWatchingDao()
    private val tvShowsDao = FakeTvShowsDao()
    private val transactionRunner = object : DatabaseTransactionRunner {
        override fun <T> invoke(block: () -> T): T = block()
    }

    private lateinit var fetcher: ProgressContinueWatchingFetcher

    @BeforeTest
    fun setUp() {
        fetcher = ProgressContinueWatchingFetcher(
            traktSyncDataSource = syncDataSource,
            traktUserDataSource = userDataSource,
            traktActivityRepository = activityRepository,
            continueWatchingDao = continueWatchingDao,
            tvShowsDao = tvShowsDao,
            transactionRunner = transactionRunner,
            datastoreRepository = FakeDatastoreRepository(),
            logger = FakeLogger(),
        )
    }

    @Test
    fun `should exclude hidden shows from candidate set`() = runTest(testDispatcher) {
        syncDataSource.setPlaybackEpisodes(
            ApiResponse.Success(listOf(breakingBadPlayback, theWirePlayback)),
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
    fun `should bootstrap candidates from watched shows given empty playback and empty dao`() = runTest(testDispatcher) {
        // Fresh install: nothing is currently paused mid-episode and the local DAO is empty.
        // The fetcher must still surface the user's full watch list so Continue Watching is not
        // empty until the user happens to pause an episode.
        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(emptyList()))
        syncDataSource.setWatchedShows(
            ApiResponse.Success(
                listOf(
                    watchedShow(showTraktId = BREAKING_BAD_ID, showTmdbId = 1396, title = "Breaking Bad"),
                    watchedShow(showTraktId = THE_WIRE_ID, showTmdbId = 1438, title = "The Wire"),
                ),
            ),
        )
        syncDataSource.setShowWatchedProgress(BREAKING_BAD_ID, ApiResponse.Success(breakingBadProgress))
        syncDataSource.setShowWatchedProgress(THE_WIRE_ID, ApiResponse.Success(theWireProgress))

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull() shouldContainExactlyInAnyOrder listOf(
            breakingBadEntry,
            theWireEntry,
        )
        syncDataSource.watchedShowsInvocations(page = 1) shouldBe 1
    }

    @Test
    fun `should include cached dao entries as candidates given playback misses them`() = runTest(testDispatcher) {
        // The Wire is no longer in playback (user finished an episode cleanly), but the local
        // Continue Watching cache still tracks it. The fetcher must refresh the cached row
        // via per-show progress so the watchlist does not silently drop it.
        continueWatchingDao.upsert(theWireEntry)
        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(listOf(breakingBadPlayback)))
        syncDataSource.setShowWatchedProgress(BREAKING_BAD_ID, ApiResponse.Success(breakingBadProgress))
        syncDataSource.setShowWatchedProgress(THE_WIRE_ID, ApiResponse.Success(theWireProgress))

        val result = fetcher.run(forceRefresh = false)

        result.shouldNotBeNull() shouldContainExactlyInAnyOrder listOf(
            breakingBadEntry,
            theWireEntry,
        )
        syncDataSource.showWatchedProgressInvocations(THE_WIRE_ID) shouldBe 1
    }

    @Test
    fun `should pass cursor to per-show progress calls given force refresh is false`() = runTest(testDispatcher) {
        activityRepository.setEpisodesWatchedSyncTimeStamp(Instant.parse(CURSOR_ISO))
        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(listOf(breakingBadPlayback)))
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
        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(listOf(breakingBadPlayback)))
        syncDataSource.setShowWatchedProgress(
            BREAKING_BAD_ID,
            ApiResponse.Success(breakingBadProgress),
        )

        fetcher.run(forceRefresh = true)

        syncDataSource.showWatchedProgressLastActivity(BREAKING_BAD_ID) shouldBe null
    }

    @Test
    fun `should filter rows where next episode is null`() = runTest(testDispatcher) {
        syncDataSource.setPlaybackEpisodes(ApiResponse.Success(listOf(resetShowPlayback)))
        syncDataSource.setShowWatchedProgress(
            RESET_SHOW_ID,
            ApiResponse.Success(resetShowProgress),
        )

        val result = fetcher.run(forceRefresh = false)

        result.shouldBeEmpty()
    }

    @Test
    fun `should return entries given populated playback and empty hidden`() = runTest(testDispatcher) {
        syncDataSource.setPlaybackEpisodes(
            ApiResponse.Success(listOf(breakingBadPlayback, theWirePlayback)),
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
    fun `should signal skip given playback call fails`() = runTest(testDispatcher) {
        syncDataSource.setPlaybackEpisodes(ApiResponse.Error.HttpError(code = 500, errorBody = "boom", errorMessage = "boom"))

        val result = fetcher.run(forceRefresh = false)

        result shouldBe null
    }

    @Test
    fun `should skip rows where per-show progress call fails`() = runTest(testDispatcher) {
        syncDataSource.setPlaybackEpisodes(
            ApiResponse.Success(listOf(breakingBadPlayback, theWirePlayback)),
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
private const val CURSOR_ISO = "2026-05-19T08:30:00Z"

private val breakingBadPlayback = TraktPlaybackEpisodeResponse(
    id = 100001,
    progress = 45.0,
    pausedAt = "2026-05-10T20:15:00.000Z",
    type = "episode",
    episode = TraktNextEpisodeResponse(
        seasonNumber = 4,
        episodeNumber = 1,
        ids = EpisodeIds(trakt = 401, tmdb = 62094),
    ),
    show = TraktShowResponse(
        title = "Breaking Bad",
        ids = ShowIds(trakt = BREAKING_BAD_ID, slug = "breaking-bad", tmdb = 1396),
        airedEpisodes = 62,
    ),
)

private val theWirePlayback = TraktPlaybackEpisodeResponse(
    id = 100002,
    progress = 20.0,
    pausedAt = "2026-04-22T09:00:00.000Z",
    type = "episode",
    episode = TraktNextEpisodeResponse(
        seasonNumber = 1,
        episodeNumber = 13,
        ids = EpisodeIds(trakt = 113, tmdb = null),
    ),
    show = TraktShowResponse(
        title = "The Wire",
        ids = ShowIds(trakt = THE_WIRE_ID, slug = "the-wire", tmdb = 1438),
        airedEpisodes = 60,
    ),
)

private val resetShowPlayback = TraktPlaybackEpisodeResponse(
    id = 100500,
    progress = 10.0,
    pausedAt = "2026-05-15T18:00:00.000Z",
    type = "episode",
    episode = TraktNextEpisodeResponse(
        seasonNumber = 1,
        episodeNumber = 1,
        ids = EpisodeIds(trakt = 9001, tmdb = null),
    ),
    show = TraktShowResponse(
        title = "Some Reset Show",
        ids = ShowIds(trakt = RESET_SHOW_ID, slug = "some-reset-show", tmdb = 9000),
        airedEpisodes = 20,
    ),
)

private val breakingBadProgress = TraktWatchedProgressResponse(
    aired = 62,
    completed = 30,
    lastWatchedAt = "2026-05-10T20:15:00Z",
    nextEpisode = nextEpisode(seasonNumber = 4, episodeNumber = 1),
)

private val theWireProgress = TraktWatchedProgressResponse(
    aired = 60,
    completed = 12,
    lastWatchedAt = "2026-04-22T09:00:00Z",
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

private fun watchedShow(
    showTraktId: Long,
    showTmdbId: Long?,
    title: String = "Show $showTraktId",
): TraktWatchedShowResponse = TraktWatchedShowResponse(
    plays = 1,
    lastWatchedAt = "2026-05-10T20:15:00Z",
    lastUpdatedAt = "2026-05-10T20:15:00Z",
    show = TraktShowResponse(
        title = title,
        ids = ShowIds(trakt = showTraktId, slug = "show-$showTraktId", tmdb = showTmdbId),
    ),
)
