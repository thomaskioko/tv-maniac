package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_1_EPISODE_COUNT
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_1_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_1_NUMBER
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_2_EPISODE_COUNT
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_2_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_2_NUMBER
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.TEST_SHOW_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.TEST_SHOW_NAME
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.TEST_SHOW_OVERVIEW
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

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
        watchedEpisodeDao = DefaultWatchedEpisodeDao(
            database = database,
            nextEpisodeDao = nextEpisodeDao,
            dispatchers = coroutineDispatcher,
        )

        insertTestData()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should persist episode with correct metadata when marked as watched`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        watchedEpisodeDao.markAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
            watchedAt = timestamp,
        )

        val lastWatched = watchedEpisodeDao.getLastWatchedEpisode(TEST_SHOW_ID)
        lastWatched.shouldNotBeNull()
        lastWatched.episode_id shouldBe Id(101L)
        lastWatched.season_number shouldBe SEASON_1_NUMBER
        lastWatched.episode_number shouldBe 1L
    }

    @Test
    fun `should remove episode from watch history when unmarked`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        watchedEpisodeDao.markAsWatched(TEST_SHOW_ID, 101L, SEASON_1_NUMBER, 1L, timestamp)
        watchedEpisodeDao.markAsUnwatched(TEST_SHOW_ID, 101L)

        val lastWatched = watchedEpisodeDao.getLastWatchedEpisode(TEST_SHOW_ID)
        lastWatched.shouldBeNull()
    }

    @Test
    fun `should mark all episodes from 1 to target when catching up`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        watchedEpisodeDao.markEpisodeAndPreviousAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 105L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 5L,
            timestamp = timestamp,
        )

        val watchedEpisodes = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_1_NUMBER)
        watchedEpisodes shouldHaveSize 5
        watchedEpisodes.map { it.episode_number }.sorted() shouldBe listOf(1L, 2L, 3L, 4L, 5L)
        watchedEpisodes.all { it.watched_at == timestamp } shouldBe true
    }

    @Test
    fun `should mark all previous seasons when catching up to later season`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        watchedEpisodeDao.markEpisodeAndPreviousAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 202L,
            seasonNumber = SEASON_2_NUMBER,
            episodeNumber = 2L,
            timestamp = timestamp,
        )

        val season1Watched = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_1_NUMBER)
        season1Watched shouldHaveSize SEASON_1_EPISODE_COUNT

        val season2Watched = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_2_NUMBER)
        season2Watched shouldHaveSize 2
        season2Watched.map { it.episode_number }.sorted() shouldBe listOf(1L, 2L)
    }

    @Test
    fun `should not duplicate already watched episodes when catching up`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        watchedEpisodeDao.markAsWatched(TEST_SHOW_ID, 101L, SEASON_1_NUMBER, 1L, timestamp)
        watchedEpisodeDao.markAsWatched(TEST_SHOW_ID, 103L, SEASON_1_NUMBER, 3L, timestamp)

        watchedEpisodeDao.markEpisodeAndPreviousAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 105L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 5L,
            timestamp = timestamp,
        )

        val watchedEpisodes = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_1_NUMBER)
        watchedEpisodes shouldHaveSize 5
    }

    @Test
    fun `should mark current and all previous seasons when completing season`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        watchedEpisodeDao.markSeasonAndPreviousAsWatched(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_2_NUMBER,
            timestamp = timestamp,
        )

        val season1Watched = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_1_NUMBER)
        season1Watched shouldHaveSize SEASON_1_EPISODE_COUNT

        val season2Watched = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_2_NUMBER)
        season2Watched shouldHaveSize SEASON_2_EPISODE_COUNT
    }

    @Test
    fun `should not affect later seasons when completing an earlier season`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        watchedEpisodeDao.markSeasonAndPreviousAsWatched(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_1_NUMBER,
            timestamp = timestamp,
        )

        val season1Watched = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_1_NUMBER)
        season1Watched shouldHaveSize SEASON_1_EPISODE_COUNT

        val season2Watched = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_2_NUMBER)
        season2Watched.shouldBeEmpty()
    }

    @Test
    fun `should apply same timestamp to all episodes when marking season watched`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val episodes = watchedEpisodeDao.getEpisodesForSeason(TEST_SHOW_ID, SEASON_1_NUMBER)

        watchedEpisodeDao.markSeasonAsWatched(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_1_NUMBER,
            episodes = episodes,
            timestamp = timestamp,
        )

        val watchedEpisodes = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_1_NUMBER)
        watchedEpisodes shouldHaveSize SEASON_1_EPISODE_COUNT
        watchedEpisodes.all { it.watched_at == timestamp } shouldBe true
    }

    @Test
    fun `should remove only episodes from specified season when unmarking`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val episodes = watchedEpisodeDao.getEpisodesForSeason(TEST_SHOW_ID, SEASON_1_NUMBER)
        watchedEpisodeDao.markSeasonAsWatched(TEST_SHOW_ID, SEASON_1_NUMBER, episodes, timestamp)

        watchedEpisodeDao.markSeasonAsUnwatched(TEST_SHOW_ID, SEASON_1_NUMBER)

        val watchedEpisodes = watchedEpisodeDao.getWatchedEpisodesForSeason(TEST_SHOW_ID, SEASON_1_NUMBER)
        watchedEpisodes.shouldBeEmpty()
    }

    @Test
    fun `should return true for watched and false for unwatched episode`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        watchedEpisodeDao.isEpisodeWatched(TEST_SHOW_ID, SEASON_1_NUMBER, 1L) shouldBe false

        watchedEpisodeDao.markAsWatched(TEST_SHOW_ID, 101L, SEASON_1_NUMBER, 1L, timestamp)

        watchedEpisodeDao.isEpisodeWatched(TEST_SHOW_ID, SEASON_1_NUMBER, 1L) shouldBe true
        watchedEpisodeDao.isEpisodeWatched(TEST_SHOW_ID, SEASON_1_NUMBER, 2L) shouldBe false
    }

    @Test
    fun `should return only skipped episodes when watching out of order`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        watchedEpisodeDao.markAsWatched(TEST_SHOW_ID, 101L, SEASON_1_NUMBER, 1L, timestamp)
        watchedEpisodeDao.markAsWatched(TEST_SHOW_ID, 103L, SEASON_1_NUMBER, 3L, timestamp)

        val unwatched = watchedEpisodeDao.getUnwatchedEpisodesBefore(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 5L,
        )

        unwatched shouldHaveSize 2
        unwatched.map { it.episodeNumber }.sorted() shouldBe listOf(2L, 4L)
    }

    @Test
    fun `should return all unwatched episodes from earlier seasons`() = runTest {
        val unwatched = watchedEpisodeDao.getUnwatchedEpisodesInPreviousSeasons(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_2_NUMBER,
        )

        unwatched shouldHaveSize SEASON_1_EPISODE_COUNT
        unwatched.all { it.seasonNumber == SEASON_1_NUMBER } shouldBe true
    }

    @Test
    fun `should count all unwatched episodes in previous seasons`() = runTest {
        val count = watchedEpisodeDao.getUnwatchedEpisodeCountInPreviousSeasons(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_2_NUMBER,
        )

        count shouldBe SEASON_1_EPISODE_COUNT.toLong()
    }

    @Test
    fun `should clear entire watch history when show is deleted`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        watchedEpisodeDao.markAsWatched(TEST_SHOW_ID, 101L, SEASON_1_NUMBER, 1L, timestamp)
        watchedEpisodeDao.markAsWatched(TEST_SHOW_ID, 102L, SEASON_1_NUMBER, 2L, timestamp)
        watchedEpisodeDao.markAsWatched(TEST_SHOW_ID, 201L, SEASON_2_NUMBER, 1L, timestamp)

        watchedEpisodeDao.deleteAllForShow(TEST_SHOW_ID)

        watchedEpisodeDao.getLastWatchedEpisode(TEST_SHOW_ID).shouldBeNull()
    }

    private fun insertTestData() {
        database.tvShowQueries.upsert(
            id = Id(TEST_SHOW_ID),
            name = TEST_SHOW_NAME,
            overview = TEST_SHOW_OVERVIEW,
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

        database.seasonsQueries.upsert(
            id = Id(SEASON_1_ID),
            show_id = Id(TEST_SHOW_ID),
            season_number = SEASON_1_NUMBER,
            title = "Season 1",
            overview = "First season",
            episode_count = SEASON_1_EPISODE_COUNT.toLong(),
            image_url = "/season1.jpg",
        )

        database.seasonsQueries.upsert(
            id = Id(SEASON_2_ID),
            show_id = Id(TEST_SHOW_ID),
            season_number = SEASON_2_NUMBER,
            title = "Season 2",
            overview = "Second season",
            episode_count = SEASON_2_EPISODE_COUNT.toLong(),
            image_url = "/season2.jpg",
        )

        repeat(SEASON_1_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            database.episodesQueries.upsert(
                id = Id(episodeId),
                season_id = Id(SEASON_1_ID),
                show_id = Id(TEST_SHOW_ID),
                title = "Episode $episodeNumber",
                overview = "Episode $episodeNumber overview",
                episode_number = episodeNumber.toLong(),
                runtime = 45L,
                image_url = "/episode$episodeNumber.jpg",
                vote_average = 8.5,
                vote_count = 50L,
                air_date = "2023-01-0$episodeNumber",
                trakt_id = null,
            )
        }

        repeat(SEASON_2_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 200L + episodeNumber
            database.episodesQueries.upsert(
                id = Id(episodeId),
                season_id = Id(SEASON_2_ID),
                show_id = Id(TEST_SHOW_ID),
                title = "Episode $episodeNumber",
                overview = "Season 2 Episode $episodeNumber overview",
                episode_number = episodeNumber.toLong(),
                runtime = 45L,
                image_url = "/s2e$episodeNumber.jpg",
                vote_average = 9.0,
                vote_count = 75L,
                air_date = "2023-02-20",
                trakt_id = null,
            )
        }

        database.watchlistQueries.upsert(
            id = Id(TEST_SHOW_ID),
            created_at = Clock.System.now().toEpochMilliseconds(),
        )
    }
}
