package com.thomaskioko.tvmaniac.startwatching.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.ShowId
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingDao
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
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
    private val showIdByTraktId = mutableMapOf<Long, Id<ShowId>>()

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
    fun `should exclude show with future air date`() = runTest(testDispatcher) {
        insertShow(2, "Unreleased Show", year = "2099-12-31")
        followShow(2)

        dao.observeStartWatchingShows().test {
            awaitItem().shouldBeEmpty()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should exclude show that has a watched episode`() = runTest(testDispatcher) {
        insertReleasedShow(id = 3, name = "Started Show")
        insertSeason(seasonId = 30, showId = 3, seasonNumber = 1)
        insertEpisode(episodeId = 301, seasonId = 30, showId = 3, episodeNumber = 1, title = "Pilot")
        followShow(3)
        markEpisodeWatched(showId = 3, episodeId = 301, seasonNumber = 1, episodeNumber = 1)

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
    fun `should exclude show once its followed entry is removed`() = runTest(testDispatcher) {
        insertReleasedShow(id = 9, name = "Finished Show")
        followShow(9)

        dao.observeStartWatchingShows().test {
            awaitItem() shouldContainExactly listOf(expectedShow(9, "Finished Show"))

            database.followedShowsQueries.deleteByShowId(showIdByTraktId.getValue(9L))

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

    @Test
    fun `should include first episode given show has aired first season`() = runTest(testDispatcher) {
        insertReleasedShow(id = 8, name = "With Episode")
        insertSeason(seasonId = 80, showId = 8, seasonNumber = 1)
        insertEpisode(episodeId = 801, seasonId = 80, showId = 8, episodeNumber = 1, title = "Pilot")
        followShow(8)

        dao.observeStartWatchingShows().test {
            val item = awaitItem().single()
            item.traktId shouldBe 8
            item.episodeId shouldBe 801
            item.episodeTitle shouldBe "Pilot"
            item.seasonNumber shouldBe 1
            item.episodeNumber shouldBe 1
            item.runtime shouldBe 45L
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun expectedShow(id: Long, title: String): StartWatchingShow =
        StartWatchingShow(traktId = id, tmdbId = id, title = title, posterPath = "/$id.jpg", year = "2020-01-01", inLibrary = true)

    private fun insertReleasedShow(id: Long, name: String) {
        insertShow(id, name)
    }

    private fun insertShow(id: Long, name: String, year: String = "2020-01-01") {
        database.tvShowQueries.upsert(
            trakt_id = Id<TraktId>(id),
            tmdb_id = Id<TmdbId>(id),
            name = name,
            overview = "Overview for $name",
            language = "en",
            year = year,
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$id.jpg",
            backdrop_path = null,
        )
        showIdByTraktId[id] = showIdForTraktId(id)
    }

    private fun insertSeason(seasonId: Long, showId: Long, seasonNumber: Long) {
        database.seasonsQueries.upsert(
            id = Id(seasonId),
            show_id = showIdByTraktId.getValue(showId),
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
    ) {
        database.episodesQueries.upsert(
            id = Id(episodeId),
            season_id = Id(seasonId),
            show_id = showIdByTraktId.getValue(showId),
            title = title,
            overview = "Overview for $title",
            episode_number = episodeNumber,
            runtime = 45L,
            image_url = null,
            ratings = 8.0,
            vote_count = 100L,
            trakt_id = null,
            first_aired = 1_000L,
        )
    }

    private fun followShow(showId: Long, pendingAction: String = "NOTHING", followedAt: Long = 1_000L) {
        database.followedShowsQueries.upsert(
            id = null,
            showId = showIdByTraktId.getValue(showId),
            tmdbId = Id(showId),
            followedAt = followedAt,
            pendingAction = pendingAction,
        )
    }

    private fun markEpisodeWatched(showId: Long, episodeId: Long, seasonNumber: Long, episodeNumber: Long) {
        database.watchedEpisodesQueries.upsert(
            show_id = showIdByTraktId.getValue(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = 1_000L,
            pending_action = "NOTHING",
        )
    }

    private fun addToContinueWatching(showId: Long) {
        database.continueWatchingQueries.upsert(
            showId = showIdByTraktId.getValue(showId),
            tmdbId = Id(showId),
            airedEpisodes = 10L,
            completedCount = 0L,
            lastWatchedAt = 1_000L,
            lastUpdatedAt = 1_000L,
            title = null,
            year = null,
        )
    }
}
