package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
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

/**
 * Comprehensive tests for the unified episode tracking repository.
 * Tests both next episode tracking and watched episode management functionality.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultEpisodeTrackingRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var episodeRepository: EpisodeRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
        val watchedEpisodeDao = DefaultWatchedEpisodeDao(
            database = database,
            nextEpisodeDao = nextEpisodeDao,
            dispatchers = coroutineDispatcher,
        )

        episodeRepository = DefaultEpisodeRepository(
            watchedEpisodeDao = watchedEpisodeDao,
            nextEpisodeDao = nextEpisodeDao,
        )

        insertTestData()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    private fun insertTestData() {
        // Insert test show
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

        // Insert seasons
        database.seasonsQueries.upsert(
            id = Id(11L),
            show_id = Id(1L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season",
            episode_count = 7L,
            image_url = "/season1.jpg",
        )

        database.seasonsQueries.upsert(
            id = Id(12L),
            show_id = Id(1L),
            season_number = 2L,
            title = "Season 2",
            overview = "Second season",
            episode_count = 13L,
            image_url = "/season2.jpg",
        )

        // Insert episodes for Season 1 (episodes 101-107)
        repeat(7) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            database.episodesQueries.upsert(
                id = Id(episodeId),
                season_id = Id(11L),
                show_id = Id(1L),
                title = "Episode $episodeNumber",
                overview = "Episode $episodeNumber overview",
                episode_number = episodeNumber.toLong(),
                runtime = 45L,
                image_url = "/episode$episodeNumber.jpg",
                vote_average = 8.5,
                vote_count = 50L,
            )
        }

        // Insert episodes for Season 2 (episodes 201-213)
        repeat(13) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 200L + episodeNumber
            database.episodesQueries.upsert(
                id = Id(episodeId),
                season_id = Id(12L),
                show_id = Id(1L),
                title = "Episode $episodeNumber",
                overview = "Season 2 Episode $episodeNumber overview",
                episode_number = episodeNumber.toLong(),
                runtime = 45L,
                image_url = "/s2e$episodeNumber.jpg",
                vote_average = 9.0,
                vote_count = 75L,
            )
        }

        // Add show to watchlist
        database.watchlistQueries.upsert(
            id = Id(1L),
            created_at = Clock.System.now().toEpochMilliseconds(),
        )
    }

    // ===== Next Episode Functions Tests =====

    @Test
    fun `should observe next episode for show with no watched episodes`() = runTest {
        episodeRepository.observeNextEpisodeForShow(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 101L // First episode
            nextEpisode.seasonNumber shouldBe 1L
            nextEpisode.episodeNumber shouldBe 1L
            nextEpisode.episodeName shouldBe "Episode 1"
        }
    }

    @Test
    fun `should observe next episode after marking episodes watched`() = runTest {
        // Mark first episode as watched
        episodeRepository.markEpisodeAsWatched(1L, 101L, 1L, 1L)

        episodeRepository.observeNextEpisodeForShow(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 102L // Second episode
            nextEpisode.seasonNumber shouldBe 1L
            nextEpisode.episodeNumber shouldBe 2L
            nextEpisode.episodeName shouldBe "Episode 2"
        }
    }

    @Test
    fun `should return next season first episode after season complete`() = runTest {
        // Mark all Season 1 episodes as watched
        repeat(7) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            episodeRepository.markEpisodeAsWatched(
                1L,
                episodeId,
                1L,
                episodeNumber.toLong(),
            )
        }

        episodeRepository.observeNextEpisodeForShow(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 201L // First episode of Season 2
            nextEpisode.seasonNumber shouldBe 2L
            nextEpisode.episodeNumber shouldBe 1L
            nextEpisode.episodeName shouldBe "Episode 1"
        }
    }

    @Test
    fun `should return null when all episodes watched`() = runTest {
        // Mark all Season 1 episodes as watched
        repeat(7) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            episodeRepository.markEpisodeAsWatched(
                1L,
                episodeId,
                1L,
                episodeNumber.toLong(),
            )
        }
        // Mark all Season 2 episodes as watched
        repeat(13) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 200L + episodeNumber
            episodeRepository.markEpisodeAsWatched(
                1L,
                episodeId,
                2L,
                episodeNumber.toLong(),
            )
        }

        episodeRepository.observeNextEpisodeForShow(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldBeNull()
        }
    }

    @Test
    fun `should observe next episodes for watchlist`() = runTest {
        episodeRepository.observeNextEpisodesForWatchlist().test {
            val nextEpisodes = awaitItem()
            nextEpisodes shouldHaveSize 1
            nextEpisodes.first().showId shouldBe 1L
            nextEpisodes.first().episodeId shouldBe 101L
            nextEpisodes.first().showName shouldBe "Test Show 1"
        }
    }

    @Test
    fun `should update watchlist next episodes when episode marked watched`() = runTest {
        episodeRepository.observeNextEpisodesForWatchlist().test {
            // Initial state - first episode
            var nextEpisodes = awaitItem()
            nextEpisodes shouldHaveSize 1
            nextEpisodes.first().episodeId shouldBe 101L

            // Mark first episode watched
            episodeRepository.markEpisodeAsWatched(1L, 101L, 1L, 1L)

            // Should automatically update to second episode
            nextEpisodes = awaitItem()
            nextEpisodes shouldHaveSize 1
            nextEpisodes.first().episodeId shouldBe 102L
        }
    }

    // ===== Watched Episode Functions Tests =====

    @Test
    fun `should mark episode as watched`() = runTest {
        // Given & When & Then - observe watched episodes flow
        episodeRepository.observeWatchedEpisodes(1L).test {
            // Initially no episodes watched
            awaitItem().shouldBeEmpty()

            // Mark episode as watched
            episodeRepository.markEpisodeAsWatched(1L, 101L, 1L, 1L)

            // Should have 1 watched episode with correct data
            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize 1
            watchedEpisodes.first().show_id shouldBe Id(1L)
            watchedEpisodes.first().episode_id shouldBe Id(101L)
            watchedEpisodes.first().season_number shouldBe 1L
            watchedEpisodes.first().episode_number shouldBe 1L
        }
    }

    @Test
    fun `should mark episode as unwatched`() = runTest {
        // Given - observe watched episodes flow
        episodeRepository.observeWatchedEpisodes(1L).test {
            // Initially no episodes watched
            awaitItem().shouldBeEmpty()

            // When - mark episode as watched
            episodeRepository.markEpisodeAsWatched(1L, 101L, 1L, 1L)

            // Then - should have 1 watched episode
            awaitItem() shouldHaveSize 1

            // When - mark as unwatched
            episodeRepository.markEpisodeAsUnwatched(1L, 101L)

            // Then - should be empty again
            val watchedEpisodes = awaitItem()
            watchedEpisodes.shouldBeEmpty()
        }
    }

    @Test
    fun `should get last watched episode`() = runTest {
        // Initially no watched episodes
        episodeRepository.getLastWatchedEpisode(1L).shouldBeNull()

        // Mark first episode watched
        episodeRepository.markEpisodeAsWatched(1L, 101L, 1L, 1L)

        val lastWatched = episodeRepository.getLastWatchedEpisode(1L)
        lastWatched.shouldNotBeNull()
        lastWatched.episode_id shouldBe Id(101L)

        // Mark third episode watched (should return episode 3 as last)
        episodeRepository.markEpisodeAsWatched(1L, 103L, 1L, 3L)

        val newLastWatched = episodeRepository.getLastWatchedEpisode(1L)
        newLastWatched.shouldNotBeNull()
        newLastWatched.episode_id shouldBe Id(103L)
        newLastWatched.season_number shouldBe 1L
        newLastWatched.episode_number shouldBe 3L
    }

    @Test
    fun `should check if episode is watched`() = runTest {
        // Initially episode is not watched
        episodeRepository.isEpisodeWatched(1L, 1L, 1L) shouldBe false

        // Mark episode as watched
        episodeRepository.markEpisodeAsWatched(1L, 101L, 1L, 1L)

        // Now episode should be watched
        episodeRepository.isEpisodeWatched(1L, 1L, 1L) shouldBe true

        // Other episode should still not be watched
        episodeRepository.isEpisodeWatched(1L, 1L, 2L) shouldBe false
    }

    @Test
    fun `should track watch progress correctly`() = runTest {
        // Given & When & Then - observe watched episodes flow
        episodeRepository.observeWatchedEpisodes(1L).test {
            // Initially no episodes watched
            awaitItem().shouldBeEmpty()

            // Mark first episode as watched
            episodeRepository.markEpisodeAsWatched(1L, 101L, 1L, 1L)

            // Should have 1 watched episode
            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize 1
            watchedEpisodes.first().episode_id shouldBe Id(101L)
            watchedEpisodes.first().season_number shouldBe 1L
            watchedEpisodes.first().episode_number shouldBe 1L

            // Last watched episode should be episode 1
            val lastWatched = episodeRepository.getLastWatchedEpisode(1L)
            lastWatched.shouldNotBeNull()
            lastWatched.episode_id shouldBe Id(101L)
            lastWatched.season_number shouldBe 1L
            lastWatched.episode_number shouldBe 1L
        }
    }

    @Test
    fun `should clear watch history for show`() = runTest {
        // Given - observe watched episodes flow
        episodeRepository.observeWatchedEpisodes(1L).test {
            // Initially no episodes watched
            awaitItem().shouldBeEmpty()

            // When - mark several episodes as watched
            episodeRepository.markEpisodeAsWatched(1L, 101L, 1L, 1L)
            awaitItem() shouldHaveSize 1

            episodeRepository.markEpisodeAsWatched(1L, 102L, 1L, 2L)
            awaitItem() shouldHaveSize 2

            episodeRepository.markEpisodeAsWatched(1L, 103L, 1L, 3L)
            awaitItem() shouldHaveSize 3

            // When - clear all watch history
            episodeRepository.clearWatchHistoryForShow(1L)

            // Then - should be empty
            val watchedEpisodes = awaitItem()
            watchedEpisodes.shouldBeEmpty()
        }

        // Verify next episode resets to first episode
        episodeRepository.observeNextEpisodeForShow(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 101L // Back to first episode
            nextEpisode.seasonNumber shouldBe 1L
            nextEpisode.episodeNumber shouldBe 1L
        }
    }

    // ===== Integration Tests =====

    @Test
    fun `should handle cross-season episode progression`() = runTest {
        // Test that marking episodes as watched correctly updates next episode across seasons
        episodeRepository.observeNextEpisodeForShow(1L).test {
            // Start with first episode
            var nextEpisode = awaitItem()
            nextEpisode?.episodeId shouldBe 101L

            // Mark episodes 1-6 as watched
            repeat(6) { episodeIndex ->
                val episodeNumber = episodeIndex + 1
                val episodeId = 100L + episodeNumber
                episodeRepository.markEpisodeAsWatched(
                    1L,
                    episodeId,
                    1L,
                    episodeNumber.toLong(),
                )

                nextEpisode = awaitItem()
                nextEpisode?.episodeNumber shouldBe (episodeNumber + 1).toLong()
            }

            // Mark episode 7 (last of season 1) as watched
            episodeRepository.markEpisodeAsWatched(1L, 107L, 1L, 7L)

            // Should automatically jump to Season 2, Episode 1
            nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 201L // First episode of season 2
            nextEpisode.seasonNumber shouldBe 2L
            nextEpisode.episodeNumber shouldBe 1L
        }
    }

    @Test
    fun `should handle partial watch progress`() = runTest {
        // Test watching episodes out of order
        episodeRepository.markEpisodeAsWatched(1L, 103L, 1L, 3L) // Skip episodes 1 and 2

        // Should still return episode 1 as next (first unwatched)
        episodeRepository.observeNextEpisodeForShow(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 101L
            nextEpisode.episodeNumber shouldBe 1L
        }

        // Watch progress should reflect 1 watched episode
        episodeRepository.observeWatchProgress(1L).test {
            val progress = awaitItem()
            progress.totalEpisodesWatched shouldBe 1
            progress.lastSeasonWatched shouldBe 1L
            progress.lastEpisodeWatched shouldBe 3L
        }
    }
}
