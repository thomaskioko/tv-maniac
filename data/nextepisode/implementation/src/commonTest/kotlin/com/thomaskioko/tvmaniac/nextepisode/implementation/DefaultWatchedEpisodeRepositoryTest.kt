package com.thomaskioko.tvmaniac.nextepisode.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.NextEpisodeRepository
import com.thomaskioko.tvmaniac.nextepisode.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.nextepisode.api.WatchedEpisodeRepository
import com.thomaskioko.tvmaniac.nextepisode.api.model.NextEpisodeWithShow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultWatchedEpisodeRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var watchedEpisodeDao: WatchedEpisodeDao
    private lateinit var nextEpisodeRepository: FakeNextEpisodeRepository
    private lateinit var repository: WatchedEpisodeRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
        watchedEpisodeDao = DefaultWatchedEpisodeDao(database, nextEpisodeDao, coroutineDispatcher)
        nextEpisodeRepository = FakeNextEpisodeRepository(nextEpisodeDao, watchedEpisodeDao)
        repository = DefaultWatchedEpisodeRepository(watchedEpisodeDao, nextEpisodeRepository)
        insertTestData()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should mark episode as watched and refresh next episode data`() = runTest {
        // When
        repository.markEpisodeAsWatched(
            showId = 1L,
            episodeId = 101L,
            seasonNumber = 1L,
            episodeNumber = 1L
        )

        // Then - episode should be marked as watched
        val isWatched = repository.isEpisodeWatched(1L, 1, 1)
        isWatched shouldBe true

        // And next episode data should be refreshed
        nextEpisodeRepository.refreshedShowIds shouldBe listOf(1L)
    }

    @Test
    fun `should mark episode as unwatched and refresh next episode data`() = runTest {
        // Given - mark episode as watched first
        repository.markEpisodeAsWatched(1L, 101L, 1, 1)
        repository.isEpisodeWatched(1L, 1, 1) shouldBe true
        nextEpisodeRepository.refreshedShowIds.clear()

        // When
        repository.markEpisodeAsUnwatched(1L, 101L)

        // Then - episode should be unwatched
        val isWatched = repository.isEpisodeWatched(1L, 1, 1)
        isWatched shouldBe false

        // And next episode data should be refreshed
        nextEpisodeRepository.refreshedShowIds shouldBe listOf(1L)
    }

    @Test
    fun `should observe watched episodes`() = runTest {
        // Given - mark episodes as watched
        repository.markEpisodeAsWatched(1L, 101L, 1, 1)
        repository.markEpisodeAsWatched(1L, 102L, 1, 2)

        // When & Then
        repository.observeWatchedEpisodes(1L).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.size shouldBe 2

            val episode1 = watchedEpisodes.find { it.episode_number == 1L }
            episode1.shouldNotBeNull()
            // Episode marked as watched (tracking app)

            val episode2 = watchedEpisodes.find { it.episode_number == 2L }
            episode2.shouldNotBeNull()
        }
    }

    @Test
    fun `should observe watch progress`() = runTest {
        // Given - setup next episode data and mark episodes as watched
        val nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
        nextEpisodeDao.upsert(1L, 103L, "Next Episode", 3, 1, "2024-01-15", 45, null, "Next to watch", false)

        repository.markEpisodeAsWatched(1L, 101L, 1, 1)
        repository.markEpisodeAsWatched(1L, 102L, 1, 2)

        // When & Then
        repository.observeWatchProgress(1L).test {
            val watchProgress = awaitItem()
            watchProgress.showId shouldBe 1L
            watchProgress.totalEpisodesWatched shouldBe 2
            watchProgress.lastSeasonWatched shouldBe 1
            watchProgress.lastEpisodeWatched shouldBe 2
            watchProgress.nextEpisode.shouldNotBeNull()
        }
    }

    @Test
    fun `should get last watched episode`() = runTest {
        // Given - mark multiple episodes as watched
        repository.markEpisodeAsWatched(1L, 101L, 1, 1)
        repository.markEpisodeAsWatched(1L, 102L, 1, 2)
        repository.markEpisodeAsWatched(1L, 201L, 2, 1)

        // When
        val lastWatched = repository.getLastWatchedEpisode(1L)

        // Then - should return season 2, episode 1 (highest absolute number)
        lastWatched.shouldNotBeNull()
        lastWatched.season_number shouldBe 2L
        lastWatched.episode_number shouldBe 1L
        lastWatched.episode_id.id shouldBe 201L
    }

    @Test
    fun `should return null for last watched episode when none exist`() = runTest {
        // When
        val lastWatched = repository.getLastWatchedEpisode(999L)

        // Then
        lastWatched.shouldBeNull()
    }

    @Test
    fun `should check if episode is watched`() = runTest {
        // Given - mark one episode as watched
        repository.markEpisodeAsWatched(1L, 101L, 1, 1)

        // When & Then
        repository.isEpisodeWatched(1L, 1, 1) shouldBe true
        repository.isEpisodeWatched(1L, 1, 2) shouldBe false
        repository.isEpisodeWatched(1L, 2, 1) shouldBe false
        repository.isEpisodeWatched(2L, 1, 1) shouldBe false
    }

    @Test
    fun `should clear watch history for show and refresh next episode data`() = runTest {
        // Given - mark episodes for multiple shows as watched
        repository.markEpisodeAsWatched(1L, 101L, 1, 1)
        repository.markEpisodeAsWatched(1L, 102L, 1, 2)
        repository.markEpisodeAsWatched(2L, 201L, 1, 1)
        nextEpisodeRepository.refreshedShowIds.clear()

        // When
        repository.clearWatchHistoryForShow(1L)

        // Then - show 1 should have no watched episodes
        repository.observeWatchedEpisodes(1L).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.shouldBeEmpty()
        }

        // Show 2 should still have its watched episodes
        repository.observeWatchedEpisodes(2L).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.size shouldBe 1
        }

        // And next episode data should be refreshed
        nextEpisodeRepository.refreshedShowIds shouldBe listOf(1L)
    }

    @Test
    fun `should handle multiple watch progress updates with refreshes`() = runTest {
        // When - mark multiple episodes with different progress
        repository.markEpisodeAsWatched(1L, 101L, 1, 1)
        repository.markEpisodeAsWatched(1L, 102L, 1, 2)
        repository.markEpisodeAsWatched(1L, 103L, 1, 3)

        // Then - all episodes should trigger refresh
        nextEpisodeRepository.refreshedShowIds shouldBe listOf(1L, 1L, 1L)

        // And watch progress should be accurate
        repository.observeWatchedEpisodes(1L).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.size shouldBe 3
        }
    }

    @Test
    fun `should remove all show tracking data when show is untracked`() = runTest {
        // Given - mark episodes as watched
        repository.markEpisodeAsWatched(1L, 101L, 1L, 1L)
        repository.markEpisodeAsWatched(1L, 102L, 1L, 2L)

        // Add next episode data directly to database
        database.nextEpisodeCacheQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(103L),
            episode_name = "Next Episode",
            episode_number = 3L,
            season_number = 1L,
            air_date = "2023-12-01",
            runtime = 45L,
            still_path = "/still.jpg",
            overview = "Next episode overview",
            is_upcoming = 0L,
            updated_at = 1000L
        )

        // Verify data exists
        repository.observeWatchedEpisodes(1L).test {
            val initialWatchedEpisodes = awaitItem()
            initialWatchedEpisodes.size shouldBe 2
        }

        database.nextEpisodeCacheQueries.getNextEpisodeForShow(Id(1L)).executeAsOneOrNull().shouldNotBeNull()

        // When - removing show from tracking
        nextEpisodeRepository.removeShowFromTracking(1L)

        // Then - all tracking data should be removed
        repository.observeWatchedEpisodes(1L).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.shouldBeEmpty()
        }

        // Next episode data should also be removed
        database.nextEpisodeCacheQueries.getNextEpisodeForShow(Id(1L)).executeAsOneOrNull().shouldBeNull()

        // Verify the method was called on the fake repository
        nextEpisodeRepository.removedShowIds shouldContain 1L
    }

    @Test
    fun `should handle partial watch progress`() = runTest {
        // When - mark episode with partial progress
        repository.markEpisodeAsWatched(1L, 101L, 1, 1)

        // Then
        repository.observeWatchedEpisodes(1L).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.size shouldBe 1
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

    private class FakeNextEpisodeRepository(
        private val nextEpisodeDao: NextEpisodeDao? = null,
        private val watchedEpisodeDao: WatchedEpisodeDao? = null,
    ) : NextEpisodeRepository {
        val refreshedShowIds = mutableListOf<Long>()
        val removedShowIds = mutableListOf<Long>()

        override suspend fun refreshNextEpisodeData(showId: Long) {
            refreshedShowIds.add(showId)
        }

        override suspend fun fetchNextEpisode(showId: Long) = Unit

        override fun observeNextEpisodeForShow(showId: Long): Flow<NextEpisodeWithShow?> = flowOf(null)
        override fun observeNextEpisodesForWatchlist(): Flow<List<NextEpisodeWithShow>> = flowOf(emptyList())

        override suspend fun removeShowFromTracking(showId: Long) {
            removedShowIds.add(showId)
            // Use real DAOs if available for integration testing
            nextEpisodeDao?.delete(showId)
            watchedEpisodeDao?.deleteAllForShow(showId)
        }
    }
}
