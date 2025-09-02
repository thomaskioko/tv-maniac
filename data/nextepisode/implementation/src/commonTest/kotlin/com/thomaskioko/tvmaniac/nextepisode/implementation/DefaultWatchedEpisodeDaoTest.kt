package com.thomaskioko.tvmaniac.nextepisode.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.WatchedEpisodeDao
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultWatchedEpisodeDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var watchedEpisodeDao: WatchedEpisodeDao
    private lateinit var nextEpisodeDao: NextEpisodeDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
        watchedEpisodeDao = DefaultWatchedEpisodeDao(database, nextEpisodeDao, coroutineDispatcher)
        insertTestData()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should observe watched episodes for show`() = runTest {
        // Given - mark episodes as watched
        watchedEpisodeDao.markAsWatched(
            showId = 1L,
            episodeId = 101L,
            seasonNumber = 1L,
            episodeNumber = 1L
        )
        watchedEpisodeDao.markAsWatched(
            showId = 1L,
            episodeId = 102L,
            seasonNumber = 1L,
            episodeNumber = 2L
        )

        // When & Then
        watchedEpisodeDao.observeWatchedEpisodes(1L).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.size shouldBe 2

            val episode1 = watchedEpisodes.find { it.episode_number == 1L }
            episode1.shouldNotBeNull()
            episode1.show_id.id shouldBe 1L
            episode1.episode_id.id shouldBe 101L
            episode1.season_number shouldBe 1L

            val episode2 = watchedEpisodes.find { it.episode_number == 2L }
            episode2.shouldNotBeNull()
            episode2.show_id.id shouldBe 1L
            episode2.episode_id.id shouldBe 102L
            episode2.season_number shouldBe 1L

        }
    }

    @Test
    fun `should observe watch progress for show`() = runTest {
        // Given - add some episodes to cache and mark some as watched
        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = 101L,
            episodeName = "Pilot",
            episodeNumber = 1L,
            seasonNumber = 1L,
            airDate = "2023-01-01",
            runtime = 45,
            stillPath = "/pilot.jpg",
            overview = "The first episode",
            isUpcoming = false
        )

        watchedEpisodeDao.markAsWatched(
            showId = 1L,
            episodeId = 101L,
            seasonNumber = 1L,
            episodeNumber = 1L
        )
        watchedEpisodeDao.markAsWatched(
            showId = 1L,
            episodeId = 102L,
            seasonNumber = 1L,
            episodeNumber = 2L
        )

        // When & Then
        watchedEpisodeDao.observeWatchProgress(1L).test {
            val watchProgress = awaitItem()
            watchProgress.showId shouldBe 1L
            watchProgress.totalEpisodesWatched shouldBe 2
            watchProgress.lastSeasonWatched shouldBe 1L
            watchProgress.lastEpisodeWatched shouldBe 2L
            watchProgress.nextEpisode.shouldNotBeNull()
        }
    }

    @Test
    fun `should mark episode as watched`() = runTest {
        // When
        watchedEpisodeDao.markAsWatched(
            showId = 1L,
            episodeId = 101L,
            seasonNumber = 1L,
            episodeNumber = 1L
        )

        // Then
        val isWatched = watchedEpisodeDao.isEpisodeWatched(1L, 1, 1)
        isWatched shouldBe true

        val watchedEpisodes = database.watchedEpisodesQueries
            .getWatchedEpisodes(Id(1L))
            .executeAsList()

        watchedEpisodes.size shouldBe 1
        val episode = watchedEpisodes.first()
        episode.show_id.id shouldBe 1L
        episode.episode_id.id shouldBe 101L
        episode.season_number shouldBe 1L
        episode.episode_number shouldBe 1L
    }

    @Test
    fun `should mark episode as unwatched`() = runTest {
        // Given - mark episode as watched first
        watchedEpisodeDao.markAsWatched(1L, 101L, 1, 1)
        watchedEpisodeDao.isEpisodeWatched(1L, 1, 1) shouldBe true

        // When
        watchedEpisodeDao.markAsUnwatched(1L, 101L)

        // Then
        val isWatched = watchedEpisodeDao.isEpisodeWatched(1L, 1, 1)
        isWatched shouldBe false
    }

    @Test
    fun `should get last watched episode`() = runTest {
        // Given - mark multiple episodes as watched
        watchedEpisodeDao.markAsWatched(1L, 101L, 1, 1)
        watchedEpisodeDao.markAsWatched(1L, 102L, 1, 2)
        watchedEpisodeDao.markAsWatched(1L, 201L, 2, 1)

        // When
        val lastWatched = watchedEpisodeDao.getLastWatchedEpisode(1L)

        // Then - should return season 2, episode 1 (highest absolute number)
        lastWatched.shouldNotBeNull()
        lastWatched.season_number shouldBe 2L
        lastWatched.episode_number shouldBe 1L
        lastWatched.episode_id.id shouldBe 201L
    }

    @Test
    fun `should return null for last watched episode when none exist`() = runTest {
        // When
        val lastWatched = watchedEpisodeDao.getLastWatchedEpisode(999L)

        // Then
        lastWatched.shouldBeNull()
    }

    @Test
    fun `should get watched episodes for specific season`() = runTest {
        // Given - mark episodes in different seasons as watched
        watchedEpisodeDao.markAsWatched(1L, 101L, 1, 1)
        watchedEpisodeDao.markAsWatched(1L, 102L, 1, 2)
        watchedEpisodeDao.markAsWatched(1L, 201L, 2, 1)
        watchedEpisodeDao.markAsWatched(1L, 202L, 2, 2)

        // When
        val season1Episodes = watchedEpisodeDao.getWatchedEpisodesForSeason(1L, 1)
        val season2Episodes = watchedEpisodeDao.getWatchedEpisodesForSeason(1L, 2)

        // Then
        season1Episodes.size shouldBe 2
        season1Episodes.all { it.season_number == 1L } shouldBe true
        season1Episodes.map { it.episode_number }.toSet() shouldBe setOf(1L, 2L)

        season2Episodes.size shouldBe 2
        season2Episodes.all { it.season_number == 2L } shouldBe true
        season2Episodes.map { it.episode_number }.toSet() shouldBe setOf(1L, 2L)
    }

    @Test
    fun `should check if episode is watched`() = runTest {
        // Given - mark one episode as watched
        watchedEpisodeDao.markAsWatched(1L, 101L, 1, 1)

        // When & Then
        watchedEpisodeDao.isEpisodeWatched(1L, 1, 1) shouldBe true
        watchedEpisodeDao.isEpisodeWatched(1L, 1, 2) shouldBe false
        watchedEpisodeDao.isEpisodeWatched(1L, 2, 1) shouldBe false
        watchedEpisodeDao.isEpisodeWatched(2L, 1, 1) shouldBe false
    }

    @Test
    fun `should delete all watched episodes for show`() = runTest {
        // Given - mark episodes for multiple shows as watched
        watchedEpisodeDao.markAsWatched(1L, 101L, 1, 1)
        watchedEpisodeDao.markAsWatched(1L, 102L, 1, 2)
        watchedEpisodeDao.markAsWatched(2L, 201L, 1, 1)

        // When
        watchedEpisodeDao.deleteAllForShow(1L)

        // Then
        watchedEpisodeDao.observeWatchedEpisodes(1L).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.shouldBeEmpty()
        }

        // Show 2 should still have its watched episodes
        watchedEpisodeDao.observeWatchedEpisodes(2L).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.size shouldBe 1
        }
    }

    @Test
    fun `should handle watch progress calculation with absolute episode numbers`() = runTest {
        // Given - mark episodes in mixed order (should calculate last watched by absolute number)
        watchedEpisodeDao.markAsWatched(1L, 102L, 1, 2)
        watchedEpisodeDao.markAsWatched(1L, 301L, 3, 1)
        watchedEpisodeDao.markAsWatched(1L, 201L, 2, 5)

        // When
        watchedEpisodeDao.observeWatchProgress(1L).test {
            val watchProgress = awaitItem()

            // Then - should identify season 3, episode 1 as last watched (highest absolute number)
            watchProgress.lastSeasonWatched shouldBe 3L
            watchProgress.lastEpisodeWatched shouldBe 1L
            watchProgress.totalEpisodesWatched shouldBe 3
        }
    }

    private fun insertTestData() {
        // Insert test TV shows
        database.tvShowQueries.upsert(
            id = Id(1),
            name = "Test Show 1",
            overview = "Test overview 1",
            language = "en",
            first_air_date = "2023-01-01",
            vote_average = 8.0,
            vote_count = 100,
            popularity = 95.0,
            genre_ids = listOf(1, 2),
            status = "Returning Series",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/test1.jpg",
            backdrop_path = "/backdrop1.jpg",
        )

        database.tvShowQueries.upsert(
            id = Id(2),
            name = "Test Show 2",
            overview = "Test overview 2",
            language = "en",
            first_air_date = "2023-02-01",
            vote_average = 7.5,
            vote_count = 200,
            popularity = 85.0,
            genre_ids = listOf(2, 3),
            status = "Ended",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/test2.jpg",
            backdrop_path = "/backdrop2.jpg",
        )
    }
}
