package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.api.WatchedEpisodeEntry
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

        watchedEpisodeDao = DefaultWatchedEpisodeDao(
            database = database,
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
    fun `should count all unwatched episodes in previous seasons`() = runTest {
        val count = watchedEpisodeDao.getUnwatchedEpisodeCountInPreviousSeasons(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_2_NUMBER,
            includeSpecials = false,
        )

        count shouldBe SEASON_1_EPISODE_COUNT.toLong()
    }

    @Test
    fun `should return all seasons watch progress with correct counts`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        watchedEpisodeDao.markAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
            watchedAt = timestamp,
            includeSpecials = false,
        )
        watchedEpisodeDao.markAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 102L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 2L,
            watchedAt = timestamp,
            includeSpecials = false,
        )
        watchedEpisodeDao.markAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
            watchedAt = timestamp,
            includeSpecials = false,
        )
        watchedEpisodeDao.markAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 201L,
            seasonNumber = SEASON_2_NUMBER,
            episodeNumber = 1L,
            watchedAt = timestamp,
            includeSpecials = false,
        )

        watchedEpisodeDao.observeAllSeasonsWatchProgress(TEST_SHOW_ID).test {
            val progress = awaitItem()
            progress shouldHaveSize 2

            val season1Progress = progress.first { it.seasonNumber == SEASON_1_NUMBER }
            season1Progress.watchedCount shouldBe 3
            season1Progress.totalCount shouldBe SEASON_1_EPISODE_COUNT
            season1Progress.progressPercentage shouldBe (3f / SEASON_1_EPISODE_COUNT)

            val season2Progress = progress.first { it.seasonNumber == SEASON_2_NUMBER }
            season2Progress.watchedCount shouldBe 1
            season2Progress.totalCount shouldBe SEASON_2_EPISODE_COUNT
            season2Progress.progressPercentage shouldBe (1f / SEASON_2_EPISODE_COUNT)
        }
    }

    @Test
    fun `should return zero watched count when no episodes watched`() = runTest {
        watchedEpisodeDao.observeAllSeasonsWatchProgress(TEST_SHOW_ID).test {
            val progress = awaitItem()
            progress shouldHaveSize 2

            val season1Progress = progress.first { it.seasonNumber == SEASON_1_NUMBER }
            season1Progress.watchedCount shouldBe 0
            season1Progress.totalCount shouldBe SEASON_1_EPISODE_COUNT
            season1Progress.progressPercentage shouldBe 0f

            val season2Progress = progress.first { it.seasonNumber == SEASON_2_NUMBER }
            season2Progress.watchedCount shouldBe 0
            season2Progress.totalCount shouldBe SEASON_2_EPISODE_COUNT
            season2Progress.progressPercentage shouldBe 0f
        }
    }

    @Test
    fun `should return empty list for non-existent show`() = runTest {
        watchedEpisodeDao.observeAllSeasonsWatchProgress(999L).test {
            val progress = awaitItem()
            progress.shouldBeEmpty()
        }
    }

    @Test
    fun `should return full progress when all episodes watched`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        repeat(SEASON_1_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            watchedEpisodeDao.markAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = episodeId,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = episodeNumber.toLong(),
                watchedAt = timestamp,
                includeSpecials = false,
            )
        }

        watchedEpisodeDao.observeAllSeasonsWatchProgress(TEST_SHOW_ID).test {
            val progress = awaitItem()
            val season1Progress = progress.first { it.seasonNumber == SEASON_1_NUMBER }
            season1Progress.watchedCount shouldBe SEASON_1_EPISODE_COUNT
            season1Progress.totalCount shouldBe SEASON_1_EPISODE_COUNT
            season1Progress.progressPercentage shouldBe 1f
        }
    }

    @Test
    fun `should update progress when episode is marked as watched`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()

        watchedEpisodeDao.observeAllSeasonsWatchProgress(TEST_SHOW_ID).test {
            val initialProgress = awaitItem()
            initialProgress.first { it.seasonNumber == SEASON_1_NUMBER }.watchedCount shouldBe 0

            watchedEpisodeDao.markAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 101L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 1L,
                watchedAt = timestamp,
                includeSpecials = false,
            )

            val updatedProgress = awaitItem()
            updatedProgress.first { it.seasonNumber == SEASON_1_NUMBER }.watchedCount shouldBe 1
        }
    }

    @Test
    fun `should batch upsert multiple episodes in single transaction`() = runTest {
        val now = Clock.System.now()
        val entries = (1..5).map { episodeNumber ->
            WatchedEpisodeEntry(
                id = 0,
                showId = TEST_SHOW_ID,
                episodeId = (100L + episodeNumber),
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = episodeNumber.toLong(),
                watchedAt = now,
                traktId = (1000L + episodeNumber),
            )
        }

        watchedEpisodeDao.upsertBatchFromTrakt(
            showId = TEST_SHOW_ID,
            entries = entries,
            includeSpecials = false,
        )

        watchedEpisodeDao.observeWatchedEpisodes(TEST_SHOW_ID).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize 5
        }

        watchedEpisodeDao.observeSeasonWatchProgress(TEST_SHOW_ID, SEASON_1_NUMBER).test {
            val progress = awaitItem()
            progress.watchedCount shouldBe 5
            progress.totalCount shouldBe SEASON_1_EPISODE_COUNT
        }
    }

    @Test
    fun `should handle empty batch upsert gracefully`() = runTest {
        watchedEpisodeDao.upsertBatchFromTrakt(
            showId = TEST_SHOW_ID,
            entries = emptyList(),
            includeSpecials = false,
        )

        watchedEpisodeDao.observeWatchedEpisodes(TEST_SHOW_ID).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes.shouldBeEmpty()
        }
    }

    private fun insertTestData() {
        val _ = database.tvShowQueries.upsert(
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

        val _ = database.seasonsQueries.upsert(
            id = Id(SEASON_1_ID),
            show_id = Id(TEST_SHOW_ID),
            season_number = SEASON_1_NUMBER,
            title = "Season 1",
            overview = "First season",
            episode_count = SEASON_1_EPISODE_COUNT.toLong(),
            image_url = "/season1.jpg",
        )

        val _ = database.seasonsQueries.upsert(
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
            val _ = database.episodesQueries.upsert(
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
            val _ = database.episodesQueries.upsert(
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

        val _ = database.followedShowsQueries.upsert(
            id = null,
            tmdbId = TEST_SHOW_ID,
            followedAt = Clock.System.now().toEpochMilliseconds(),
            pendingAction = "NOTHING",
            traktId = null,
        )
    }
}
