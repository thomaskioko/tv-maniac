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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

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
    private var watchDate = LocalDate(2024, 1, 1)
        .atStartOfDayIn(TimeZone.UTC).epochSeconds

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
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(101L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = watchDate,
        )
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(102L),
            season_number = 1L,
            episode_number = 2L,
            watched_at = watchDate,
        )

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
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(101L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = watchDate,
        )
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(102L),
            season_number = 1L,
            episode_number = 2L,
            watched_at = watchDate,
        )
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(103L),
            season_number = 1L,
            episode_number = 3L,
            watched_at = watchDate,
        )

        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldBeNull()
        }
    }

    @Test
    fun `should observe next episodes for watchlist`() = runTest {
        val _ = database.watchlistQueries.upsert(
            id = Id(1L),
            created_at = watchDate,
        )
        val _ = database.watchlistQueries.upsert(
            id = Id(2L),
            created_at = watchDate + 1000,
        )

        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(101L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = watchDate,
        )

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
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(3L),
            episode_id = Id(301L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = watchDate,
        )
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(3L),
            episode_id = Id(302L),
            season_number = 1L,
            episode_number = 2L,
            watched_at = watchDate,
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
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(101L),
            season_number = 1L,
            episode_number = 1L,
            watched_at = watchDate,
        )
        val _ = database.watchedEpisodesQueries.upsert(
            show_id = Id(1L),
            episode_id = Id(103L),
            season_number = 1L,
            episode_number = 3L,
            watched_at = watchDate,
        )

        // Since the test data only has 3 episodes and E3 is the last watched, there are no more episodes to progress to
        nextEpisodeDao.observeNextEpisode(1L).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldBeNull()
        }
    }

    private fun insertTestData() {
        // Insert test TV shows
        val _ = database.tvShowQueries.upsert(
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

        val _ = database.tvShowQueries.upsert(
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
        val _ = database.seasonsQueries.upsert(
            id = Id(11L),
            show_id = Id(1L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season",
            episode_count = 3L,
            image_url = "/season1.jpg",
        )

        // Insert episodes for show 1
        val _ = database.episodesQueries.upsert(
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
            air_date = "2023-01-01",
            trakt_id = null,
        )

        val _ = database.episodesQueries.upsert(
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
            air_date = "2023-01-08",
            trakt_id = null,
        )

        val _ = database.episodesQueries.upsert(
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
            air_date = "2023-01-15",
            trakt_id = null,
        )

        // Insert season for show 2
        val _ = database.seasonsQueries.upsert(
            id = Id(21L),
            show_id = Id(2L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season of show 2",
            episode_count = 2L,
            image_url = "/show2season1.jpg",
        )

        // Insert episodes for show 2
        val _ = database.episodesQueries.upsert(
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
            air_date = "2023-02-01",
            trakt_id = null,
        )

        val _ = database.episodesQueries.upsert(
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
            air_date = "2023-02-08",
            trakt_id = null,
        )
    }

    private fun insertShow3WithMultipleSeasons() {
        val _ = database.tvShowQueries.upsert(
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
        val _ = database.seasonsQueries.upsert(
            id = Id(31L),
            show_id = Id(3L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season",
            episode_count = 2L,
            image_url = "/s1.jpg",
        )

        val _ = database.episodesQueries.upsert(
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
            air_date = "2022-01-01",
            trakt_id = null,
        )

        val _ = database.episodesQueries.upsert(
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
            air_date = "2022-01-08",
            trakt_id = null,
        )

        // Season 2
        val _ = database.seasonsQueries.upsert(
            id = Id(32L),
            show_id = Id(3L),
            season_number = 2L,
            title = "Season 2",
            overview = "Second season",
            episode_count = 2L,
            image_url = "/s2.jpg",
        )

        val _ = database.episodesQueries.upsert(
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
            air_date = "2022-02-01",
            trakt_id = null,
        )
    }

    private fun insertShow4WithSpecials() {
        val _ = database.tvShowQueries.upsert(
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
        val _ = database.seasonsQueries.upsert(
            id = Id(40L),
            show_id = Id(4L),
            season_number = 0L,
            title = "Specials",
            overview = "Special episodes",
            episode_count = 1L,
            image_url = "/s0.jpg",
        )

        val _ = database.episodesQueries.upsert(
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
            air_date = "2022-12-25",
            trakt_id = null,
        )

        // Season 1
        val _ = database.seasonsQueries.upsert(
            id = Id(41L),
            show_id = Id(4L),
            season_number = 1L,
            title = "Season 1",
            overview = "Regular season",
            episode_count = 1L,
            image_url = "/s1.jpg",
        )

        val _ = database.episodesQueries.upsert(
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
            air_date = "2023-01-01",
            trakt_id = null,
        )
    }

    @Test
    fun `should maintain shows in watchlist when tracking sequentially`() = runTest {
        //  Track Breaking Bad (show 1)
        val _ = database.watchlistQueries.upsert(
            id = Id(1L),
            created_at = watchDate,
        )

        // Verify Breaking Bad episode appears
        nextEpisodeDao.observeNextEpisodesForWatchlist().test {
            val episodes1 = awaitItem()
            episodes1.size shouldBe 1
            episodes1[0].showId shouldBe 1L
            episodes1[0].showName shouldBe "Test Show 1"

            // Step 2: Track Game of Thrones (show 2) - this should ADD to the list, not replace
            val _ = database.watchlistQueries.upsert(
                id = Id(2L),
                created_at = watchDate + 1000,
            )

            val episodes2 = awaitItem()
            episodes2.size shouldBe 2

            episodes2[0].showId shouldBe 2L
            episodes2[0].showName shouldBe "Test Show 2"

            episodes2[1].showId shouldBe 1L
            episodes2[1].showName shouldBe "Test Show 1"
        }
    }

    @Test
    fun `should handle shows with Specials seasons correctly`() = runTest {
        val _ = database.tvShowQueries.upsert(
            id = Id(5L),
            name = "Breaking Bad",
            overview = "Chemistry teacher turns to cooking meth",
            language = "en",
            first_air_date = "2008-01-20",
            vote_average = 9.3,
            vote_count = 5000,
            popularity = 95.0,
            genre_ids = listOf(1, 2),
            status = "Ended",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/bb.jpg",
            backdrop_path = "/bb-back.jpg",
        )

        // Season 0 (Specials) - should be excluded
        val _ = database.seasonsQueries.upsert(
            id = Id(50L),
            show_id = Id(5L),
            season_number = 0L,
            title = "Specials",
            overview = "Special episodes",
            episode_count = 1L,
            image_url = "/bb-specials.jpg",
        )

        val _ = database.episodesQueries.upsert(
            id = Id(500L),
            season_id = Id(50L),
            show_id = Id(5L),
            title = "BB Special Episode",
            overview = "Behind the scenes",
            episode_number = 1L,
            runtime = 30L,
            image_url = "/bb-special.jpg",
            vote_average = 7.0,
            vote_count = 100L,
            air_date = "2008-01-10",
            trakt_id = null,
        )

        // Season 1 (Regular) - should be included
        val _ = database.seasonsQueries.upsert(
            id = Id(51L),
            show_id = Id(5L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season",
            episode_count = 2L,
            image_url = "/bb-s1.jpg",
        )

        val _ = database.episodesQueries.upsert(
            id = Id(501L),
            season_id = Id(51L),
            show_id = Id(5L),
            title = "Pilot",
            overview = "Walter White starts cooking",
            episode_number = 1L,
            runtime = 58L,
            image_url = "/bb-pilot.jpg",
            vote_average = 9.0,
            vote_count = 1000L,
            air_date = "2008-01-20",
            trakt_id = null,
        )

        val _ = database.episodesQueries.upsert(
            id = Id(502L),
            season_id = Id(51L),
            show_id = Id(5L),
            title = "Cat's in the Bag...",
            overview = "Walter and Jesse dispose of evidence",
            episode_number = 2L,
            runtime = 48L,
            image_url = "/bb-ep2.jpg",
            vote_average = 8.8,
            vote_count = 950L,
            air_date = "2008-01-27",
            trakt_id = null,
        )

        // Create Game of Thrones (show 6) with Specials
        val _ = database.tvShowQueries.upsert(
            id = Id(6L),
            name = "Game of Thrones",
            overview = "Power struggles in Westeros",
            language = "en",
            first_air_date = "2011-04-17",
            vote_average = 9.3,
            vote_count = 8000,
            popularity = 98.0,
            genre_ids = listOf(3, 4),
            status = "Ended",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/got.jpg",
            backdrop_path = "/got-back.jpg",
        )

        // Season 0 (Specials) - should be excluded
        val _ = database.seasonsQueries.upsert(
            id = Id(60L),
            show_id = Id(6L),
            season_number = 0L,
            title = "Specials",
            overview = "Behind the scenes and extras",
            episode_count = 2L,
            image_url = "/got-specials.jpg",
        )

        val _ = database.episodesQueries.upsert(
            id = Id(600L),
            season_id = Id(60L),
            show_id = Id(6L),
            title = "Making of GOT",
            overview = "Documentary",
            episode_number = 1L,
            runtime = 45L,
            image_url = "/got-making.jpg",
            vote_average = 8.0,
            vote_count = 200L,
            air_date = "2011-04-01",
            trakt_id = null,
        )

        val _ = database.episodesQueries.upsert(
            id = Id(601L),
            season_id = Id(60L),
            show_id = Id(6L),
            title = "GOT Interviews",
            overview = "Cast interviews",
            episode_number = 2L,
            runtime = 60L,
            image_url = "/got-interviews.jpg",
            vote_average = 7.5,
            vote_count = 150L,
            air_date = "2011-04-10",
            trakt_id = null,
        )

        // Season 1 (Regular) - should be included
        val _ = database.seasonsQueries.upsert(
            id = Id(61L),
            show_id = Id(6L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season of GOT",
            episode_count = 2L,
            image_url = "/got-s1.jpg",
        )

        val _ = database.episodesQueries.upsert(
            id = Id(610L),
            season_id = Id(61L),
            show_id = Id(6L),
            title = "Winter Is Coming",
            overview = "The Starks receive troubling news",
            episode_number = 1L,
            runtime = 62L,
            image_url = "/got-winter.jpg",
            vote_average = 9.0,
            vote_count = 2000L,
            air_date = "2011-04-17",
            trakt_id = null,
        )

        val _ = database.episodesQueries.upsert(
            id = Id(611L),
            season_id = Id(61L),
            show_id = Id(6L),
            title = "The Kingsroad",
            overview = "Ned travels to King's Landing",
            episode_number = 2L,
            runtime = 56L,
            image_url = "/got-kingsroad.jpg",
            vote_average = 8.7,
            vote_count = 1800L,
            air_date = "2011-04-24",
            trakt_id = null,
        )

        // Now simulate the user scenario:
        // 1. Track Breaking Bad
        val _ = database.watchlistQueries.upsert(
            id = Id(5L), // Breaking Bad
            created_at = watchDate,
        )

        nextEpisodeDao.observeNextEpisodesForWatchlist().test {
            // Should show Breaking Bad S1E1 (Specials should be excluded)
            val episodes1 = awaitItem()
            episodes1.size shouldBe 1
            episodes1[0].showId shouldBe 5L
            episodes1[0].showName shouldBe "Breaking Bad"
            episodes1[0].episodeName shouldBe "Pilot"
            episodes1[0].seasonNumber shouldBe 1
            episodes1[0].episodeNumber shouldBe 1

            // 2. Track Game of Thrones - this should ADD, not replace
            val _ = database.watchlistQueries.upsert(
                id = Id(6L), // Game of Thrones
                created_at = watchDate + 1000,
            )

            // Should show BOTH shows (GOT first due to being more recent)
            val episodes2 = awaitItem()
            episodes2.size shouldBe 2 // KEY: This might fail if the bug exists

            // GOT should be first (more recently added)
            episodes2[0].showId shouldBe 6L
            episodes2[0].showName shouldBe "Game of Thrones"
            episodes2[0].episodeName shouldBe "Winter Is Coming"
            episodes2[0].seasonNumber shouldBe 1
            episodes2[0].episodeNumber shouldBe 1

            // Breaking Bad should still be there (not replaced due to Specials issue)
            episodes2[1].showId shouldBe 5L
            episodes2[1].showName shouldBe "Breaking Bad"
            episodes2[1].episodeName shouldBe "Pilot"
            episodes2[1].seasonNumber shouldBe 1
            episodes2[1].episodeNumber shouldBe 1
        }
    }
}
