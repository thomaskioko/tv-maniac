package com.thomaskioko.tvmaniac.startwatching.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingDao
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultStartWatchingDaoTest : BaseDatabaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var dao: StartWatchingDao

    @BeforeTest
    fun setUp() {
        dao = DefaultStartWatchingDao(database, dispatchers)
    }

    @AfterTest
    fun tearDown() {
        closeDb()
    }

    @Test
    fun `should include followed released show with no watched episodes`() = runTest(testDispatcher) {
        insertReleasedShow(id = 1, name = "Breaking Bad")
        followShow(1)

        dao.observeStartWatchingShows().test {
            awaitItem() shouldContainExactly listOf(expectedShow(1, "Breaking Bad"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should exclude unreleased show with no aired episodes`() = runTest(testDispatcher) {
        insertShow(2, "Unreleased Show")
        insertSeason(seasonId = 20, showId = 2, seasonNumber = 1)
        insertEpisode(episodeId = 200, seasonId = 20, showId = 2, episodeNumber = 1, title = "Premiere", firstAired = FAR_FUTURE)
        followShow(2)

        dao.observeStartWatchingShows().test {
            awaitItem().shouldBeEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should exclude show that has a watched episode`() = runTest(testDispatcher) {
        insertReleasedShow(id = 3, name = "Started Show")
        followShow(3)
        markEpisodeWatched(showId = 3, episodeId = 300 + 1, seasonNumber = 1, episodeNumber = 1)

        dao.observeStartWatchingShows().test {
            awaitItem().shouldBeEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should exclude show present in continue watching`() = runTest(testDispatcher) {
        insertReleasedShow(id = 4, name = "Continue Watching Show")
        followShow(4)
        addToContinueWatching(4)

        dao.observeStartWatchingShows().test {
            awaitItem().shouldBeEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should exclude show pending unfollow`() = runTest(testDispatcher) {
        insertReleasedShow(id = 5, name = "Unfollowed Show")
        followShow(5, pendingAction = "DELETE")

        dao.observeStartWatchingShows().test {
            awaitItem().shouldBeEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should order by followed at descending`() = runTest(testDispatcher) {
        insertReleasedShow(id = 6, name = "Older Follow")
        insertReleasedShow(id = 7, name = "Newer Follow")
        followShow(6, followedAt = 1_000L)
        followShow(7, followedAt = 2_000L)

        dao.observeStartWatchingShows().test {
            awaitItem() shouldContainExactly listOf(
                expectedShow(7, "Newer Follow"),
                expectedShow(6, "Older Follow"),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun expectedShow(id: Long, title: String): StartWatchingShow =
        StartWatchingShow(traktId = id, tmdbId = id, title = title, posterPath = "/$id.jpg", year = "2025-01-01", inLibrary = true)

    private fun insertReleasedShow(id: Long, name: String) {
        insertShow(id, name)
        insertSeason(seasonId = id * 10, showId = id, seasonNumber = 1)
        insertEpisode(
            episodeId = id * 100 + 1,
            seasonId = id * 10,
            showId = id,
            episodeNumber = 1,
            title = "Pilot",
            firstAired = AIRED,
        )
    }

    private fun insertShow(id: Long, name: String) {
        database.tvShowQueries.upsert(
            trakt_id = Id<TraktId>(id),
            tmdb_id = Id<TmdbId>(id),
            name = name,
            overview = "Overview for $name",
            language = "en",
            year = "2025-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$id.jpg",
            backdrop_path = null,
        )
    }

    private fun insertSeason(seasonId: Long, showId: Long, seasonNumber: Long) {
        database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_trakt_id = Id<TraktId>(showId),
            season_number = seasonNumber,
            title = "Season $seasonNumber",
            overview = "Overview",
            episode_count = 10L,
            image_url = null,
        )
    }

    private fun insertEpisode(
        episodeId: Long,
        seasonId: Long,
        showId: Long,
        episodeNumber: Long,
        title: String,
        firstAired: Long?,
    ) {
        database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(seasonId),
            show_trakt_id = Id<TraktId>(showId),
            title = title,
            overview = "Overview for $title",
            episode_number = episodeNumber,
            runtime = 45L,
            image_url = null,
            ratings = 8.0,
            vote_count = 100L,
            trakt_id = null,
            first_aired = firstAired,
        )
    }

    private fun followShow(showId: Long, pendingAction: String = "NOTHING", followedAt: Long = 1_000L) {
        database.followedShowsQueries.upsert(
            id = null,
            traktId = Id(showId),
            tmdbId = Id(showId),
            followedAt = followedAt,
            pendingAction = pendingAction,
        )
    }

    private fun markEpisodeWatched(showId: Long, episodeId: Long, seasonNumber: Long, episodeNumber: Long) {
        database.watchedEpisodesQueries.upsert(
            show_trakt_id = Id<TraktId>(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = 1_000L,
            pending_action = "NOTHING",
        )
    }

    private fun addToContinueWatching(showId: Long) {
        database.traktContinueWatchingQueries.upsert(
            traktId = Id(showId),
            tmdbId = Id(showId),
            airedEpisodes = 10L,
            completedCount = 0L,
            lastWatchedAt = 1_000L,
            lastUpdatedAt = 1_000L,
            title = null,
            year = null,
        )
    }

    private companion object {
        private const val AIRED = 1_000L
        private const val FAR_FUTURE = 9_999_999_999_999L
    }
}
