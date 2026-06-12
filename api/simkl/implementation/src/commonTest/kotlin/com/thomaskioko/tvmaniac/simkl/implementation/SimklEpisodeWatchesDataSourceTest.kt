package com.thomaskioko.tvmaniac.simkl.implementation

import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
import com.thomaskioko.tvmaniac.followedshows.api.PendingAction
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAddHistoryResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklAllItemsResponse
import com.thomaskioko.tvmaniac.simkl.api.model.SimklRemoveHistoryResponse
import com.thomaskioko.tvmaniac.simkl.testing.FakeSimklSyncRemoteDataSource
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Instant

internal class SimklEpisodeWatchesDataSourceTest {

    private val fakeRemote = FakeSimklSyncRemoteDataSource()
    private val dataSource = SimklEpisodeWatchesDataSource(syncRemoteDataSource = fakeRemote)

    @Test
    fun `should map show metadata and episodes given getAllWatchedShows returns watched show`() = runTest {
        fakeRemote.setAllWatchedShows(ApiResponse.Success(SIMKL_RESPONSE))

        val batches = dataSource.getAllWatchedShows(page = 1, limit = 100)

        batches shouldHaveSize 1
        val batch = batches.first()
        batch.tmdbId shouldBe 62417L
        batch.imdbId shouldBe "tt3579018"
        batch.title shouldBe "Emerald City"
        batch.providerShowId shouldBe "583436"
        batch.episodes shouldHaveSize 1
        val episode = batch.episodes.first()
        episode.seasonNumber shouldBe 1L
        episode.episodeNumber shouldBe 1L
        episode.watchedAt shouldBe Instant.parse("2024-09-01T09:10:11Z")
        episode.traktId.shouldBeNull()
        episode.pendingAction shouldBe PendingAction.NOTHING
    }

    @Test
    fun `should return empty list given getAllWatchedShows is called with page greater than one`() = runTest {
        fakeRemote.setAllWatchedShows(ApiResponse.Success(SIMKL_RESPONSE))

        val batches = dataSource.getAllWatchedShows(page = 2, limit = 100)

        batches.shouldBeEmpty()
    }

    @Test
    fun `should return empty list given getAllWatchedShows returns unauthenticated`() = runTest {
        fakeRemote.setAllWatchedShows(ApiResponse.Unauthenticated)

        val batches = dataSource.getAllWatchedShows(page = 1, limit = 100)

        batches.shouldBeEmpty()
    }

    @Test
    fun `should skip episodes with null watched_at given show has episodes missing timestamps`() = runTest {
        fakeRemote.setAllWatchedShows(ApiResponse.Success(SIMKL_RESPONSE_NULL_WATCHED_AT))

        val batches = dataSource.getAllWatchedShows(page = 1, limit = 100)

        batches shouldHaveSize 1
        batches.first().episodes.shouldBeEmpty()
    }

    @Test
    fun `should use tmdb id zero for showId given show has no tmdb id`() = runTest {
        fakeRemote.setAllWatchedShows(ApiResponse.Success(SIMKL_RESPONSE_NO_TMDB))

        val batches = dataSource.getAllWatchedShows(page = 1, limit = 100)

        batches shouldHaveSize 1
        val batch = batches.first()
        batch.tmdbId.shouldBeNull()
        batch.episodes.first().showId shouldBe 0L
    }

    @Test
    fun `should add history entries given addEpisodeEntries receives watched entries`() = runTest {
        fakeRemote.setAddHistoryResponse(ApiResponse.Success(SimklAddHistoryResponse()))
        val entries = listOf(
            WatchedEpisodeEntry(
                showId = 583436L,
                episodeId = null,
                seasonNumber = 1L,
                episodeNumber = 1L,
                watchedAt = Instant.parse("2024-09-01T09:10:11Z"),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        dataSource.addEpisodeEntries(entries)
    }

    @Test
    fun `should remove history entries given removeEpisodeEntries receives watched entries`() = runTest {
        fakeRemote.setRemoveHistoryResponse(ApiResponse.Success(SimklRemoveHistoryResponse()))
        val entries = listOf(
            WatchedEpisodeEntry(
                showId = 583436L,
                episodeId = null,
                seasonNumber = 1L,
                episodeNumber = 1L,
                watchedAt = Instant.parse("2024-09-01T09:10:11Z"),
                pendingAction = PendingAction.NOTHING,
            ),
        )

        dataSource.removeEpisodeEntries(entries)
    }
}

private val SIMKL_RESPONSE = SimklAllItemsResponse(
    shows = listOf(
        com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedShow(
            status = "completed",
            show = com.thomaskioko.tvmaniac.simkl.api.model.SimklShowEntry(
                title = "Emerald City",
                ids = com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds(
                    simkl = 583436L,
                    tmdb = "62417",
                    imdb = "tt3579018",
                    tvdb = "295779",
                ),
            ),
            seasons = listOf(
                com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedSeason(
                    number = 1,
                    episodes = listOf(
                        com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedEpisode(
                            number = 1,
                            watchedAt = "2024-09-01T09:10:11Z",
                        ),
                    ),
                ),
            ),
        ),
    ),
)

private val SIMKL_RESPONSE_NULL_WATCHED_AT = SimklAllItemsResponse(
    shows = listOf(
        com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedShow(
            status = "watching",
            show = com.thomaskioko.tvmaniac.simkl.api.model.SimklShowEntry(
                title = "No Timestamp Show",
                ids = com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds(simkl = 100L, tmdb = "99"),
            ),
            seasons = listOf(
                com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedSeason(
                    number = 1,
                    episodes = listOf(
                        com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedEpisode(number = 1, watchedAt = null),
                    ),
                ),
            ),
        ),
    ),
)

private val SIMKL_RESPONSE_NO_TMDB = SimklAllItemsResponse(
    shows = listOf(
        com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedShow(
            status = "watching",
            show = com.thomaskioko.tvmaniac.simkl.api.model.SimklShowEntry(
                title = "No TMDB Show",
                ids = com.thomaskioko.tvmaniac.simkl.api.model.SimklShowIds(simkl = 200L, imdb = "tt9999"),
            ),
            seasons = listOf(
                com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedSeason(
                    number = 1,
                    episodes = listOf(
                        com.thomaskioko.tvmaniac.simkl.api.model.SimklWatchedEpisode(number = 1, watchedAt = "2024-01-01T00:00:00Z"),
                    ),
                ),
            ),
        ),
    ),
)
