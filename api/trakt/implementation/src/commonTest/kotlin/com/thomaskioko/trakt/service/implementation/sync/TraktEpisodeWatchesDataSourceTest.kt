package com.thomaskioko.trakt.service.implementation.sync

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.followedshows.testing.FakeFollowedShowsDao
import com.thomaskioko.tvmaniac.trakt.api.model.ShowIds
import com.thomaskioko.tvmaniac.trakt.api.model.TraktShowResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedEpisodeResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedSeasonResponse
import com.thomaskioko.tvmaniac.trakt.api.model.TraktWatchedShowResponse
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktEpisodeHistoryRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.testing.FakeTraktSyncRemoteDataSource
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

internal class TraktEpisodeWatchesDataSourceTest {

    private val fakeSync = FakeTraktSyncRemoteDataSource()
    private val dataSource = TraktEpisodeWatchesDataSource(
        remoteDataSource = FakeTraktEpisodeHistoryRemoteDataSource(),
        syncRemoteDataSource = fakeSync,
        followedShowsDao = FakeFollowedShowsDao(),
    )

    @Test
    fun `should request extended progress and map seasons and episodes given watched shows returned`() = runTest {
        fakeSync.setWatchedShows(ApiResponse.Success(listOf(BREAKING_BAD)))

        val batches = dataSource.getAllWatchedShows(page = 1, limit = 100)

        fakeSync.watchedShowsExtended(1) shouldBe "progress"
        batches shouldHaveSize 1
        val batch = batches.first()
        batch.tmdbId shouldBe 1396L
        batch.imdbId shouldBe "tt0903747"
        batch.title shouldBe "Breaking Bad"
        batch.providerShowId shouldBe "1388"
        batch.episodes shouldHaveSize 2
        val episode = batch.episodes.first()
        episode.showId shouldBe 1396L
        episode.seasonNumber shouldBe 1L
        episode.episodeNumber shouldBe 1L
        episode.watchedAt shouldBe Instant.parse("2026-04-25T20:00:00.000Z")
        episode.traktId.shouldBeNull()
        episode.pendingAction shouldBe PendingAction.NOTHING
    }

    @Test
    fun `should return empty list given getAllWatchedShows returns unauthenticated`() = runTest {
        fakeSync.setWatchedShows(ApiResponse.Unauthenticated)

        val batches = dataSource.getAllWatchedShows(page = 1, limit = 100)

        batches.shouldBeEmpty()
    }

    @Test
    fun `should throw given getAllWatchedShows returns error`() = runTest {
        fakeSync.setWatchedShows(ApiResponse.Error.HttpError(code = 500, errorBody = null, errorMessage = "boom"))

        shouldThrow<BulkWatchedShowsFetchException> {
            dataSource.getAllWatchedShows(page = 1, limit = 100)
        }
    }

    @Test
    fun `should skip episodes with null last_watched_at given show has episodes missing timestamps`() = runTest {
        fakeSync.setWatchedShows(ApiResponse.Success(listOf(SHOW_WITH_NULL_WATCHED_AT)))

        val batches = dataSource.getAllWatchedShows(page = 1, limit = 100)

        batches shouldHaveSize 1
        batches.first().episodes.shouldBeEmpty()
    }

    @Test
    fun `should drop show given seasons are null`() = runTest {
        fakeSync.setWatchedShows(ApiResponse.Success(listOf(SHOW_WITHOUT_SEASONS)))

        val batches = dataSource.getAllWatchedShows(page = 1, limit = 100)

        batches.shouldBeEmpty()
    }
}

private val BREAKING_BAD = TraktWatchedShowResponse(
    plays = 56,
    lastWatchedAt = "2026-04-26T22:15:42.000Z",
    lastUpdatedAt = "2026-04-26T22:15:42.000Z",
    show = TraktShowResponse(
        title = "Breaking Bad",
        year = 2008,
        ids = ShowIds(trakt = 1388, tmdb = 1396, slug = "breaking-bad", imdb = "tt0903747", tvdb = 81189),
    ),
    seasons = listOf(
        TraktWatchedSeasonResponse(
            number = 1,
            episodes = listOf(
                TraktWatchedEpisodeResponse(number = 1, plays = 1, lastWatchedAt = "2026-04-25T20:00:00.000Z"),
                TraktWatchedEpisodeResponse(number = 2, plays = 1, lastWatchedAt = "2026-04-26T22:15:42.000Z"),
            ),
        ),
    ),
)

private val SHOW_WITH_NULL_WATCHED_AT = TraktWatchedShowResponse(
    plays = 1,
    show = TraktShowResponse(
        title = "No Timestamp Show",
        ids = ShowIds(trakt = 99, tmdb = 42),
    ),
    seasons = listOf(
        TraktWatchedSeasonResponse(
            number = 1,
            episodes = listOf(TraktWatchedEpisodeResponse(number = 1, plays = 1, lastWatchedAt = null)),
        ),
    ),
)

private val SHOW_WITHOUT_SEASONS = TraktWatchedShowResponse(
    plays = 1,
    show = TraktShowResponse(
        title = "No Seasons Show",
        ids = ShowIds(trakt = 100, tmdb = 200),
    ),
    seasons = null,
)
