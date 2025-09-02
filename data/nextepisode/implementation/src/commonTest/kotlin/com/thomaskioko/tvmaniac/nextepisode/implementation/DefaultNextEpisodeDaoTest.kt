package com.thomaskioko.tvmaniac.nextepisode.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeDao
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
import kotlin.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultNextEpisodeDaoTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var nextEpisodeDao: NextEpisodeDao

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
        insertTestData()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should observe next episode for show`() = runTest {
        // Given - upsert next episode for show
        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = 101L,
            episodeName = "Next Episode",
            episodeNumber = 3L,
            seasonNumber = 1L,
            airDate = "2024-01-15",
            runtime = 45,
            stillPath = "/next-episode.jpg",
            overview = "The next episode to watch",
            isUpcoming = false
        )

        // When & Then
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.showId shouldBe 1L
            nextEpisode.showName shouldBe "Test Show 1"
            nextEpisode.episodeId shouldBe 101L
            nextEpisode.episodeName shouldBe "Next Episode"
            nextEpisode.seasonNumber shouldBe 1
            nextEpisode.episodeNumber shouldBe 3
            nextEpisode.airDate shouldBe "2024-01-15"
            nextEpisode.runtime shouldBe 45
            nextEpisode.stillPath shouldBe "/next-episode.jpg"
            nextEpisode.overview shouldBe "The next episode to watch"
            nextEpisode.isUpcoming shouldBe false
        }
    }

    @Test
    fun `should return null when no next episode exists`() = runTest {
        // When & Then
        nextEpisodeDao.observeNextEpisode(999L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldBeNull()
        }
    }

    @Test
    fun `should observe next episodes for watchlist`() = runTest {
        // Given - add shows to watchlist
        database.watchlistQueries.upsert(
            id = Id(1L),
            created_at = Clock.System.now().toEpochMilliseconds()
        )
        database.watchlistQueries.upsert(
            id = Id(2L),
            created_at = Clock.System.now().toEpochMilliseconds()
        )

        // Add next episodes for both shows
        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = 101L,
            episodeName = "Episode 3",
            episodeNumber = 3L,
            seasonNumber = 1L,
            airDate = "2024-01-15",
            runtime = 45,
            stillPath = "/episode3.jpg",
            overview = "Episode 3 overview",
            isUpcoming = false
        )

        nextEpisodeDao.upsert(
            showId = 2L,
            episodeId = 201L,
            episodeName = "Episode 5",
            episodeNumber = 5L,
            seasonNumber = 2L,
            airDate = "2024-01-20",
            runtime = 50,
            stillPath = "/episode5.jpg",
            overview = "Episode 5 overview",
            isUpcoming = true
        )

        // When & Then
        nextEpisodeDao.observeNextEpisodesForWatchlist().test {
            val watchlistEpisodes = awaitItem()
            watchlistEpisodes.size shouldBe 2

            val show1Episode = watchlistEpisodes.find { it.showId == 1L }
            show1Episode.shouldNotBeNull()
            show1Episode.showName shouldBe "Test Show 1"
            show1Episode.episodeName shouldBe "Episode 3"
            show1Episode.isUpcoming shouldBe false
            show1Episode.followedAt.shouldNotBeNull()

            val show2Episode = watchlistEpisodes.find { it.showId == 2L }
            show2Episode.shouldNotBeNull()
            show2Episode.showName shouldBe "Test Show 2"
            show2Episode.episodeName shouldBe "Episode 5"
            show2Episode.isUpcoming shouldBe true
            show2Episode.followedAt.shouldNotBeNull()
        }
    }

    @Test
    fun `should upsert next episode with all fields`() = runTest {
        // When
        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = 150L,
            episodeName = "Season Finale",
            episodeNumber = 10L,
            seasonNumber = 2L,
            airDate = "2024-03-20",
            runtime = 60,
            stillPath = "/finale.jpg",
            overview = "The epic season finale",
            isUpcoming = true
        )

        // Then
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 150L
            nextEpisode.episodeName shouldBe "Season Finale"
            nextEpisode.episodeNumber shouldBe 10
            nextEpisode.seasonNumber shouldBe 2
            nextEpisode.airDate shouldBe "2024-03-20"
            nextEpisode.runtime shouldBe 60
            nextEpisode.stillPath shouldBe "/finale.jpg"
            nextEpisode.overview shouldBe "The epic season finale"
            nextEpisode.isUpcoming shouldBe true
        }
    }

    @Test
    fun `should upsert next episode with null optional fields`() = runTest {
        // When
        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = null,
            episodeName = "Unknown Episode",
            episodeNumber = 1L,
            seasonNumber = 1L,
            airDate = null,
            runtime = null,
            stillPath = null,
            overview = "No details available",
            isUpcoming = false
        )

        // Then
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId.shouldBeNull()
            nextEpisode.episodeName shouldBe "Unknown Episode"
            nextEpisode.airDate.shouldBeNull()
            nextEpisode.runtime.shouldBeNull()
            nextEpisode.stillPath.shouldBeNull()
            nextEpisode.overview shouldBe "No details available"
            nextEpisode.isUpcoming shouldBe false
        }
    }

    @Test
    fun `should update existing next episode on upsert`() = runTest {
        // Given - insert initial episode
        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = 101L,
            episodeName = "Initial Episode",
            episodeNumber = 1L,
            seasonNumber = 1L,
            airDate = "2024-01-01",
            runtime = 30,
            stillPath = "/initial.jpg",
            overview = "Initial overview",
            isUpcoming = true
        )

        // When - update with new data
        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = 102L,
            episodeName = "Updated Episode",
            episodeNumber = 2L,
            seasonNumber = 1L,
            airDate = "2024-01-08",
            runtime = 45,
            stillPath = "/updated.jpg",
            overview = "Updated overview",
            isUpcoming = false
        )

        // Then - should have updated data
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 102L
            nextEpisode.episodeName shouldBe "Updated Episode"
            nextEpisode.episodeNumber shouldBe 2
            nextEpisode.runtime shouldBe 45
            nextEpisode.overview shouldBe "Updated overview"
            nextEpisode.isUpcoming shouldBe false
        }
    }

    @Test
    fun `should delete next episode for show`() = runTest {
        // Given - add next episode
        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = 101L,
            episodeName = "Episode to Delete",
            episodeNumber = 1L,
            seasonNumber = 1L,
            airDate = "2024-01-01",
            runtime = 45,
            stillPath = "/delete.jpg",
            overview = "Will be deleted",
            isUpcoming = false
        )

        // Verify it exists
        nextEpisodeDao.observeNextEpisode(1L).test {
            awaitItem().shouldNotBeNull()
        }

        // When
        nextEpisodeDao.delete(1L)

        // Then
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldBeNull()
        }
    }

    @Test
    fun `should delete all next episodes`() = runTest {
        // Given - add shows to watchlist first, then add next episodes
        database.watchlistQueries.upsert(
            id = Id(1L),
            created_at = Clock.System.now().toEpochMilliseconds()
        )
        database.watchlistQueries.upsert(
            id = Id(2L),
            created_at = Clock.System.now().toEpochMilliseconds()
        )

        nextEpisodeDao.upsert(1L, 101L, "Episode 1", 1, 1, null, null, null, "Overview 1", false)
        nextEpisodeDao.upsert(2L, 201L, "Episode 2", 1, 1, null, null, null, "Overview 2", false)

        // Verify both exist
        val initialCount = nextEpisodeDao.getNextEpisodesCount()
        initialCount shouldBe 2L

        // When
        nextEpisodeDao.deleteAll()

        // Then
        val finalCount = nextEpisodeDao.getNextEpisodesCount()
        finalCount shouldBe 0L

        nextEpisodeDao.observeNextEpisode(1L).test {
            awaitItem().shouldBeNull()
        }

        nextEpisodeDao.observeNextEpisode(2L).test {
            awaitItem().shouldBeNull()
        }
    }

    @Test
    fun `should get next episodes count`() = runTest {
        // Given - initially empty
        nextEpisodeDao.getNextEpisodesCount() shouldBe 0L

        // When - add shows to watchlist first, then add episodes
        database.watchlistQueries.upsert(
            id = Id(1L),
            created_at = Clock.System.now().toEpochMilliseconds()
        )
        database.watchlistQueries.upsert(
            id = Id(2L),
            created_at = Clock.System.now().toEpochMilliseconds()
        )

        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = 101L,
            episodeName = "Episode 1",
            episodeNumber = 1,
            seasonNumber = 1,
            airDate = null,
            runtime = null,
            stillPath = null,
            overview = "Overview 1",
            isUpcoming = false
        )
        nextEpisodeDao.upsert(
            showId = 2L,
            episodeId = 201L,
            episodeName = "Episode 2",
            episodeNumber = 1,
            seasonNumber = 1,
            airDate = null,
            runtime = null,
            stillPath = null,
            overview = "Overview 2",
            isUpcoming = false
        )

        // Then
        nextEpisodeDao.getNextEpisodesCount() shouldBe 2L
    }

    @Test
    fun `should get stale next episodes`() = runTest {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val threshold = currentTime - (24 * 60 * 60 * 1000) // 24 hours ago

        // Given - add a fresh episode
        nextEpisodeDao.upsert(
            showId = 1L, 101L,
            episodeName = "Fresh Episode",
            episodeNumber = 1,
            seasonNumber = 1,
            airDate = null, runtime = null,
            stillPath = null,
            overview = "Fresh",
            isUpcoming = false
        )

        // Manually insert a stale episode with old timestamp directly into database
        val staleTimestamp = currentTime - (48 * 60 * 60 * 1000) // 48 hours ago
        database.nextEpisodeCacheQueries.upsert(
            show_id = Id(2L),
            episode_id = Id(201L),
            episode_name = "Stale Episode",
            episode_number = 1L,
            season_number = 1L,
            air_date = null,
            runtime = null,
            still_path = null,
            overview = "Stale",
            is_upcoming = 0L,
            updated_at = staleTimestamp
        )

        // When
        val staleEpisodes = nextEpisodeDao.getStaleNextEpisodes(threshold)

        // Then
        staleEpisodes.size shouldBe 1
        staleEpisodes shouldContain 2L
    }

    @Test
    fun `should handle episode number and season number conversion`() = runTest {
        // Given - upsert with specific numbers
        nextEpisodeDao.upsert(
            showId = 1L,
            episodeId = 101L,
            episodeName = "Test Episode",
            episodeNumber = 15L,
            seasonNumber = 3L,
            airDate = null,
            runtime = 42,
            stillPath = null,
            overview = "Test conversion",
            isUpcoming = false
        )

        // When & Then
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeNumber shouldBe 15
            nextEpisode.seasonNumber shouldBe 3
            nextEpisode.runtime shouldBe 42
            cancelAndConsumeRemainingEvents()
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
