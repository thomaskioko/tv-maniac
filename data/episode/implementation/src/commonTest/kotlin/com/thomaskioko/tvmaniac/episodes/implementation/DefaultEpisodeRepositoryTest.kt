package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_1_EPISODE_COUNT
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_1_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_1_NUMBER
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_2_EPISODE_COUNT
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_2_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_2_NUMBER
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.TEST_SHOW_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.TEST_SHOW_NAME
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.TEST_SHOW_OVERVIEW
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultNextEpisodeDao
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultWatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.i18n.testing.util.IgnoreIos
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
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
import kotlin.time.Clock

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

@IgnoreIos
@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultEpisodeRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val fakeDatastoreRepository = FakeDatastoreRepository()
    private val fakeDateTimeProvider = FakeDateTimeProvider()
    private val fakeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
    private val watchedEpisodeDao = DefaultWatchedEpisodeDao(
        database = database,
        dispatchers = coroutineDispatcher,
        dateTimeProvider = fakeDateTimeProvider,
    )

    private lateinit var episodeRepository: EpisodeRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        episodeRepository = DefaultEpisodeRepository(
            watchedEpisodeDao = watchedEpisodeDao,
            nextEpisodeDao = nextEpisodeDao,
            datastoreRepository = fakeDatastoreRepository,
            syncRepository = fakeSyncRepository,
        )

        insertTestData()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should return next season first episode after season complete`() = runTest {
        repeat(SEASON_1_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            episodeRepository.markEpisodeAsWatched(
                showTraktId = TEST_SHOW_ID,
                episodeId = episodeId,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = episodeNumber.toLong(),
            )
        }
    }

    @Test
    fun `should return null when all episodes watched`() = runTest {
        repeat(SEASON_1_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            episodeRepository.markEpisodeAsWatched(
                showTraktId = TEST_SHOW_ID,
                episodeId = episodeId,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = episodeNumber.toLong(),
            )
        }

        repeat(SEASON_2_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 200L + episodeNumber
            episodeRepository.markEpisodeAsWatched(
                showTraktId = TEST_SHOW_ID,
                episodeId = episodeId,
                seasonNumber = SEASON_2_NUMBER,
                episodeNumber = episodeNumber.toLong(),
            )
        }
    }

    @Test
    fun `should observe next episodes for watchlist`() = runTest {
        episodeRepository.observeNextEpisodesForWatchlist().test {
            val nextEpisodes = awaitItem()
            nextEpisodes shouldHaveSize 1
            nextEpisodes.first().showTraktId shouldBe TEST_SHOW_ID
            nextEpisodes.first().episodeId shouldBe 101L
            nextEpisodes.first().showName shouldBe TEST_SHOW_NAME
        }
    }

    @Test
    fun `should update watchlist next episodes when episode marked watched`() = runTest {
        episodeRepository.observeNextEpisodesForWatchlist().test {
            var nextEpisodes = awaitItem()
            nextEpisodes shouldHaveSize 1
            nextEpisodes.first().episodeId shouldBe 101L

            episodeRepository.markEpisodeAsWatched(
                showTraktId = TEST_SHOW_ID,
                episodeId = 101L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 1L,
            )

            nextEpisodes = awaitItem()
            nextEpisodes shouldHaveSize 1
            nextEpisodes.first().episodeId shouldBe 102L
        }
    }

    @Test
    fun `should observe all seasons watch progress with correct counts`() = runTest {
        episodeRepository.markEpisodeAsWatched(TEST_SHOW_ID, 101L, SEASON_1_NUMBER, 1L)
        episodeRepository.markEpisodeAsWatched(TEST_SHOW_ID, 102L, SEASON_1_NUMBER, 2L)
        episodeRepository.markEpisodeAsWatched(TEST_SHOW_ID, 103L, SEASON_1_NUMBER, 3L)
        episodeRepository.markEpisodeAsWatched(TEST_SHOW_ID, 201L, SEASON_2_NUMBER, 1L)

        episodeRepository.observeAllSeasonsWatchProgress(TEST_SHOW_ID).test {
            val progress = awaitItem()
            progress shouldHaveSize 2

            val season1Progress = progress.first { it.seasonNumber == SEASON_1_NUMBER }
            season1Progress.watchedCount shouldBe 3
            season1Progress.totalCount shouldBe SEASON_1_EPISODE_COUNT
            season1Progress.progressPercentage shouldBe (3f / SEASON_1_EPISODE_COUNT)

            val season2Progress = progress.first { it.seasonNumber == SEASON_2_NUMBER }
            season2Progress.watchedCount shouldBe 1
            season2Progress.totalCount shouldBe SEASON_2_EPISODE_COUNT
        }
    }

    @Test
    fun `should return empty progress for non-existent show`() = runTest {
        episodeRepository.observeAllSeasonsWatchProgress(999L).test {
            val progress = awaitItem()
            progress.shouldBeEmpty()
        }
    }

    @Test
    fun `should update all seasons progress when episode marked watched`() = runTest {
        episodeRepository.observeAllSeasonsWatchProgress(TEST_SHOW_ID).test {
            val initialProgress = awaitItem()
            initialProgress.first { it.seasonNumber == SEASON_1_NUMBER }.watchedCount shouldBe 0

            episodeRepository.markEpisodeAsWatched(TEST_SHOW_ID, 101L, SEASON_1_NUMBER, 1L)

            val updatedProgress = awaitItem()
            updatedProgress.first { it.seasonNumber == SEASON_1_NUMBER }.watchedCount shouldBe 1
        }
    }

    @Test
    fun `should return full progress percentage when season complete`() = runTest {
        repeat(SEASON_1_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            episodeRepository.markEpisodeAsWatched(TEST_SHOW_ID, episodeId, SEASON_1_NUMBER, episodeNumber.toLong())
        }

        episodeRepository.observeAllSeasonsWatchProgress(TEST_SHOW_ID).test {
            val progress = awaitItem()
            val season1Progress = progress.first { it.seasonNumber == SEASON_1_NUMBER }
            season1Progress.watchedCount shouldBe SEASON_1_EPISODE_COUNT
            season1Progress.totalCount shouldBe SEASON_1_EPISODE_COUNT
            season1Progress.progressPercentage shouldBe 1f
        }
    }

    private fun insertTestData() {
        val _ = database.tvShowQueries.upsert(
            trakt_id = Id(TEST_SHOW_ID),
            tmdb_id = Id(TEST_SHOW_ID),
            name = TEST_SHOW_NAME,
            overview = TEST_SHOW_OVERVIEW,
            language = "en",
            year = "2023-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama", "Action"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/test1.jpg",
            backdrop_path = "/backdrop1.jpg",
        )

        val _ = database.seasonsQueries.upsert(
            id = Id(SEASON_1_ID),
            show_trakt_id = Id(TEST_SHOW_ID),
            season_number = SEASON_1_NUMBER,
            title = "Season 1",
            overview = "First season",
            episode_count = SEASON_1_EPISODE_COUNT.toLong(),
            image_url = "/season1.jpg",
        )

        val _ = database.seasonsQueries.upsert(
            id = Id(SEASON_2_ID),
            show_trakt_id = Id(TEST_SHOW_ID),
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
                show_trakt_id = Id(TEST_SHOW_ID),
                title = "Episode $episodeNumber",
                overview = "Episode $episodeNumber overview",
                episode_number = episodeNumber.toLong(),
                runtime = 45L,
                image_url = "/episode$episodeNumber.jpg",
                ratings = 8.5,
                vote_count = 50L,
                trakt_id = null,
                first_aired = LocalDate(2023, 1, episodeNumber).toEpochMillis(),
            )
        }

        repeat(SEASON_2_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 200L + episodeNumber
            val _ = database.episodesQueries.upsert(
                id = Id(episodeId),
                season_id = Id(SEASON_2_ID),
                show_trakt_id = Id(TEST_SHOW_ID),
                title = "Episode $episodeNumber",
                overview = "Season 2 Episode $episodeNumber overview",
                episode_number = episodeNumber.toLong(),
                runtime = 45L,
                image_url = "/s2e$episodeNumber.jpg",
                ratings = 9.0,
                vote_count = 75L,
                trakt_id = null,
                first_aired = LocalDate(2023, 2, 20).toEpochMillis(),
            )
        }

        val _ = database.followedShowsQueries.upsert(
            id = null,
            traktId = Id(TEST_SHOW_ID),
            tmdbId = Id(TEST_SHOW_ID),
            followedAt = Clock.System.now().toEpochMilliseconds(),
            pendingAction = "NOTHING",
        )

        val _ = database.showMetadataQueries.upsert(
            show_trakt_id = Id(TEST_SHOW_ID),
            season_count = 2,
            episode_count = (SEASON_1_EPISODE_COUNT + SEASON_2_EPISODE_COUNT).toLong(),
            status = "Returning Series",
        )
    }
}
