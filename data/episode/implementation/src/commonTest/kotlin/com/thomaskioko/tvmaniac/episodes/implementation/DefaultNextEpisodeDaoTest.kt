package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.api.NextEpisodeDao
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
 * Tests for the SQL view-based next episode DAO.
 * The view automatically determines next episodes based on watched episodes,
 * so we test by manipulating watched episode data and observing the results.
 */
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
    fun `should observe next episode for show with no watched episodes`() = runTest {
        // Given - show with episodes but no watched episodes
        // The view should return the first episode (S01E01)

        // When & Then
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.showId shouldBe 1L
            nextEpisode.showName shouldBe "Test Show 1"
            nextEpisode.episodeName shouldBe "Episode 1"
            nextEpisode.seasonNumber shouldBe 1
            nextEpisode.episodeNumber shouldBe 1
        }
    }

    @Test
    fun `should observe next episode after marking episodes as watched`() = runTest {
        // Given - mark first two episodes as watched
        database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(101L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )
        database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(102L),
            season_number = 1L,
            episode_number = 2L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )

        // When & Then - should return episode 3
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeName shouldBe "Episode 3"
            nextEpisode.seasonNumber shouldBe 1
            nextEpisode.episodeNumber shouldBe 3
        }
    }

    @Test
    fun `should return null when all episodes are watched`() = runTest {
        // Given - mark all episodes as watched
        database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(101L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )
        database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(102L),
            season_number = 1L,
            episode_number = 2L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )
        database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(103L),
            season_number = 1L,
            episode_number = 3L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )

        // When & Then
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldBeNull()
        }
    }

    @Test
    fun `should observe next episodes for watchlist`() = runTest {
        // Given - add shows to watchlist
        database.watchlistQueries.upsert(
            id = Id(1L),
            created_at = Clock.System.now().toEpochMilliseconds(),
        )
        database.watchlistQueries.upsert(
            id = Id(2L),
            created_at = Clock.System.now().toEpochMilliseconds() + 1000,
        )

        // Mark some episodes as watched for show 1
        database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(101L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )

        // When & Then
        nextEpisodeDao.observeNextEpisodesForWatchlist().test {
            val watchlistEpisodes = awaitItem()
            watchlistEpisodes.size shouldBe 2

            // Show 2 should be first (more recently added to watchlist)
            val show2Episode = watchlistEpisodes[0]
            show2Episode.showId shouldBe 2L
            show2Episode.showName shouldBe "Test Show 2"
            show2Episode.episodeName shouldBe "Show 2 Episode 1"
            show2Episode.seasonNumber shouldBe 1
            show2Episode.episodeNumber shouldBe 1
            show2Episode.followedAt.shouldNotBeNull()

            // Show 1 should have episode 2 as next (episode 1 is watched)
            val show1Episode = watchlistEpisodes[1]
            show1Episode.showId shouldBe 1L
            show1Episode.showName shouldBe "Test Show 1"
            show1Episode.episodeName shouldBe "Episode 2"
            show1Episode.seasonNumber shouldBe 1
            show1Episode.episodeNumber shouldBe 2
            show1Episode.followedAt.shouldNotBeNull()
        }
    }

    @Test
    fun `should handle cross-season progression`() = runTest {
        // Given - show with multiple seasons
        insertShow3WithMultipleSeasons()

        // Mark all of season 1 as watched
        database.watchedEpisodesQueries.upsert(
            show_id = Id(3L),
            episode_id = Id(301L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )
        database.watchedEpisodesQueries.upsert(
            show_id = Id(3L),
            episode_id = Id(302L),
            season_number = 1L,
            episode_number = 2L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )

        // When & Then - should return first episode of season 2
        nextEpisodeDao.observeNextEpisode(3L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeName shouldBe "S2E1"
            nextEpisode.seasonNumber shouldBe 2
            nextEpisode.episodeNumber shouldBe 1
        }
    }

    @Test
    fun `should skip season 0 episodes`() = runTest {
        // Given - show with season 0 (specials) and season 1
        insertShow4WithSpecials()

        // When & Then - should return first episode of season 1, not season 0
        nextEpisodeDao.observeNextEpisode(4L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeName shouldBe "Regular Episode 1"
            nextEpisode.seasonNumber shouldBe 1
            nextEpisode.episodeNumber shouldBe 1
        }
    }

    @Test
    fun `should handle non-sequential watch progress`() = runTest {
        // Given - watch episodes out of order (e.g., watched E1 and E3, but not E2)
        database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(101L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )
        database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(103L),
            season_number = 1L,
            episode_number = 3L,
            watched_at = Clock.System.now().toEpochMilliseconds(),
        )

        // When & Then - should still return E2 as next
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeName shouldBe "Episode 2"
            nextEpisode.episodeNumber shouldBe 2
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

        // Insert seasons for show 1
        database.seasonsQueries.upsert(
            id = Id(11L),
            show_id = Id(1L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season",
            episode_count = 3L,
            image_url = "/season1.jpg",
        )

        // Insert episodes for show 1
        database.episodesQueries.upsert(
            id = Id(101L),
            season_id = Id(11L),
            show_id = Id(1L),
            title = "Episode 1",
            overview = "First episode",
            episode_number = 1L,
            runtime = 45L,
            image_url = "/ep1.jpg",
            vote_average = 8.0,
            vote_count = 100L,
        )

        database.episodesQueries.upsert(
            id = Id(102L),
            season_id = Id(11L),
            show_id = Id(1L),
            title = "Episode 2",
            overview = "Second episode",
            episode_number = 2L,
            runtime = 45L,
            image_url = "/ep2.jpg",
            vote_average = 8.2,
            vote_count = 110L,
        )

        database.episodesQueries.upsert(
            id = Id(103L),
            season_id = Id(11L),
            show_id = Id(1L),
            title = "Episode 3",
            overview = "Third episode",
            episode_number = 3L,
            runtime = 45L,
            image_url = "/ep3.jpg",
            vote_average = 8.5,
            vote_count = 120L,
        )

        // Insert season for show 2
        database.seasonsQueries.upsert(
            id = Id(21L),
            show_id = Id(2L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season of show 2",
            episode_count = 2L,
            image_url = "/show2season1.jpg",
        )

        // Insert episodes for show 2
        database.episodesQueries.upsert(
            id = Id(201L),
            season_id = Id(21L),
            show_id = Id(2L),
            title = "Show 2 Episode 1",
            overview = "First episode of show 2",
            episode_number = 1L,
            runtime = 50L,
            image_url = "/show2ep1.jpg",
            vote_average = 7.5,
            vote_count = 80L,
        )

        database.episodesQueries.upsert(
            id = Id(202L),
            season_id = Id(21L),
            show_id = Id(2L),
            title = "Show 2 Episode 2",
            overview = "Second episode of show 2",
            episode_number = 2L,
            runtime = 50L,
            image_url = "/show2ep2.jpg",
            vote_average = 7.8,
            vote_count = 85L,
        )
    }

    private fun insertShow3WithMultipleSeasons() {
        database.tvShowQueries.upsert(
            id = Id(3),
            name = "Multi-Season Show",
            overview = "Show with multiple seasons",
            language = "en",
            first_air_date = "2022-01-01",
            vote_average = 8.5,
            vote_count = 500,
            popularity = 100.0,
            genre_ids = listOf(1),
            status = "Returning Series",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/multi.jpg",
            backdrop_path = "/multi-back.jpg",
        )

        // Season 1
        database.seasonsQueries.upsert(
            id = Id(31L),
            show_id = Id(3L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season",
            episode_count = 2L,
            image_url = "/s1.jpg",
        )

        database.episodesQueries.upsert(
            id = Id(301L),
            season_id = Id(31L),
            show_id = Id(3L),
            title = "S1E1",
            overview = "Season 1 Episode 1",
            episode_number = 1L,
            runtime = 45L,
            image_url = "/s1e1.jpg",
            vote_average = 8.0,
            vote_count = 100L,
        )

        database.episodesQueries.upsert(
            id = Id(302L),
            season_id = Id(31L),
            show_id = Id(3L),
            title = "S1E2",
            overview = "Season 1 Episode 2",
            episode_number = 2L,
            runtime = 45L,
            image_url = "/s1e2.jpg",
            vote_average = 8.2,
            vote_count = 110L,
        )

        // Season 2
        database.seasonsQueries.upsert(
            id = Id(32L),
            show_id = Id(3L),
            season_number = 2L,
            title = "Season 2",
            overview = "Second season",
            episode_count = 2L,
            image_url = "/s2.jpg",
        )

        database.episodesQueries.upsert(
            id = Id(303L),
            season_id = Id(32L),
            show_id = Id(3L),
            title = "S2E1",
            overview = "Season 2 Episode 1",
            episode_number = 1L,
            runtime = 45L,
            image_url = "/s2e1.jpg",
            vote_average = 8.5,
            vote_count = 120L,
        )
    }

    private fun insertShow4WithSpecials() {
        database.tvShowQueries.upsert(
            id = Id(4),
            name = "Show with Specials",
            overview = "Show with season 0",
            language = "en",
            first_air_date = "2023-01-01",
            vote_average = 7.5,
            vote_count = 100,
            popularity = 80.0,
            genre_ids = listOf(1),
            status = "Returning Series",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/specials.jpg",
            backdrop_path = "/specials-back.jpg",
        )

        // Season 0 (Specials)
        database.seasonsQueries.upsert(
            id = Id(40L),
            show_id = Id(4L),
            season_number = 0L,
            title = "Specials",
            overview = "Special episodes",
            episode_count = 1L,
            image_url = "/s0.jpg",
        )

        database.episodesQueries.upsert(
            id = Id(400L),
            season_id = Id(40L),
            show_id = Id(4L),
            title = "Christmas Special",
            overview = "Special episode",
            episode_number = 1L,
            runtime = 60L,
            image_url = "/special.jpg",
            vote_average = 7.0,
            vote_count = 50L,
        )

        // Season 1
        database.seasonsQueries.upsert(
            id = Id(41L),
            show_id = Id(4L),
            season_number = 1L,
            title = "Season 1",
            overview = "Regular season",
            episode_count = 1L,
            image_url = "/s1.jpg",
        )

        database.episodesQueries.upsert(
            id = Id(401L),
            season_id = Id(41L),
            show_id = Id(4L),
            title = "Regular Episode 1",
            overview = "First regular episode",
            episode_number = 1L,
            runtime = 45L,
            image_url = "/reg1.jpg",
            vote_average = 8.0,
            vote_count = 100L,
        )
    }
}
