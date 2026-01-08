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
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.createSeasonDetailsForContinueTracking
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.testShowSeasons
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.i18n.testing.util.IgnoreIos
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
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
    private val fakeWatchlistDao = FakeWatchlistDao()
    private val fakeSeasonDetailsRepository = FakeSeasonDetailsRepository()
    private val fakeDatastoreRepository = FakeDatastoreRepository()
    private val fakeSeasonsRepository = FakeSeasonsRepository()
    private val fakeDateTimeProvider = FakeDateTimeProvider()
    private val fakeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
    private val watchedEpisodeDao = DefaultWatchedEpisodeDao(
        database = database,
        dispatchers = coroutineDispatcher,
    )
    private lateinit var episodeRepository: EpisodeRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        episodeRepository = DefaultEpisodeRepository(
            watchedEpisodeDao = watchedEpisodeDao,
            nextEpisodeDao = nextEpisodeDao,
            database = database,
            datastoreRepository = fakeDatastoreRepository,
            dispatchers = coroutineDispatcher,
            seasonsRepository = fakeSeasonsRepository,
            seasonDetailsRepository = fakeSeasonDetailsRepository,
            watchlistDao = fakeWatchlistDao,
            dateTimeProvider = fakeDateTimeProvider,
            syncRepository = lazy { fakeSyncRepository },
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
                showId = TEST_SHOW_ID,
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
                showId = TEST_SHOW_ID,
                episodeId = episodeId,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = episodeNumber.toLong(),
            )
        }

        repeat(SEASON_2_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 200L + episodeNumber
            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
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
            nextEpisodes.first().showId shouldBe TEST_SHOW_ID
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
                showId = TEST_SHOW_ID,
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
    fun `should observe last watched episode`() = runTest {
        val flow = episodeRepository.observeLastWatchedEpisode(showId = TEST_SHOW_ID)

        flow.test {
            val initialItem = awaitItem()
            initialItem.shouldBeNull()

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 103L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 3L,
            )

            val afterFirst = awaitItem()
            afterFirst.shouldNotBeNull()
            afterFirst.episodeId shouldBe 103L
            afterFirst.seasonNumber shouldBe 1
            afterFirst.episodeNumber shouldBe 3

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 105L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 5L,
            )

            val afterSecond = awaitItem()
            afterSecond.shouldNotBeNull()
            afterSecond.episodeId shouldBe 105L
            afterSecond.seasonNumber shouldBe 1
            afterSecond.episodeNumber shouldBe 5
        }
    }

    @Test
    fun `should return active season for continue tracking when user has watch progress`() =
        runTest {
            fakeSeasonsRepository.setSeasonsResult(testShowSeasons)

            val season2Episodes =
                MockData.createSeason2EpisodesWithWatchedState(watchedEpisodeNumber = 3L)
            fakeSeasonDetailsRepository.setSeasonsResult(
                createSeasonDetailsForContinueTracking(
                    seasonId = SEASON_2_ID,
                    seasonNumber = SEASON_2_NUMBER,
                    episodes = season2Episodes,
                ),
            )

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 203L,
                seasonNumber = SEASON_2_NUMBER,
                episodeNumber = 3L,
            )

            episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
                val result = awaitItem()
                result.shouldNotBeNull()
                result.currentSeasonNumber shouldBe SEASON_2_NUMBER
                result.episodes shouldHaveSize 4
                result.firstUnwatchedIndex shouldBe 0
            }
        }

    @Test
    fun `should return season 1 for continue tracking when user has no watch progress`() = runTest {
        fakeSeasonsRepository.setSeasonsResult(testShowSeasons)

        val season1Episodes = MockData.createSeason1EpisodesForContinueTracking()
        fakeSeasonDetailsRepository.setSeasonsResult(
            createSeasonDetailsForContinueTracking(
                seasonId = SEASON_1_ID,
                seasonNumber = SEASON_1_NUMBER,
                episodes = season1Episodes,
            ),
        )

        episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
            val result = awaitItem()
            result.shouldNotBeNull()
            result.currentSeasonNumber shouldBe SEASON_1_NUMBER
            result.episodes shouldHaveSize 2
            result.firstUnwatchedIndex shouldBe 0
        }
    }

    @Test
    fun `should return season with future episodes when all previous seasons watched`() = runTest {
        fakeSeasonsRepository.setSeasonsResult(testShowSeasons)

        val futureEpisodes = MockData.createFutureEpisodesForSeason(
            seasonId = SEASON_1_ID,
            seasonNumber = SEASON_1_NUMBER,
            episodeCount = 3,
            daysUntilAir = 14,
        )
        fakeSeasonDetailsRepository.setSeasonsResult(
            createSeasonDetailsForContinueTracking(
                seasonId = SEASON_1_ID,
                seasonNumber = SEASON_1_NUMBER,
                episodes = futureEpisodes,
            ),
        )

        episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
            val result = awaitItem()
            result.shouldNotBeNull()
            result.currentSeasonNumber shouldBe SEASON_1_NUMBER
            result.episodes shouldHaveSize 3
            result.episodes.all { it.daysUntilAir != null && it.daysUntilAir!! > 0 } shouldBe true
        }
    }

    @Test
    fun `should return null when all episodes in all seasons are watched`() = runTest {
        fakeSeasonsRepository.setSeasonsResult(testShowSeasons)

        val allWatchedEpisodes = MockData.createAllWatchedEpisodesForSeason(
            seasonId = SEASON_1_ID,
            seasonNumber = SEASON_1_NUMBER,
            episodeCount = 5,
        )
        fakeSeasonDetailsRepository.setSeasonsResult(
            createSeasonDetailsForContinueTracking(
                seasonId = SEASON_1_ID,
                seasonNumber = SEASON_1_NUMBER,
                episodes = allWatchedEpisodes,
            ),
        )

        episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
            val result = awaitItem()
            result.shouldBeNull()
        }
    }

    @Test
    fun `should return null for continue tracking when show not in library`() = runTest {
        fakeWatchlistDao.setIsInLibrary(false)
        fakeSeasonsRepository.setSeasonsResult(testShowSeasons)

        episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
            val result = awaitItem()
            result.shouldBeNull()
        }
    }

    @Test
    fun `should emit continue tracking result when season details become available`() = runTest {
        fakeSeasonsRepository.setSeasonsResult(testShowSeasons)
        fakeSeasonDetailsRepository.setSeasonsResult(null)

        episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
            awaitItem().shouldBeNull()

            val season1Episodes = MockData.createSeason1EpisodesForContinueTracking()
            fakeSeasonDetailsRepository.setSeasonsResult(
                createSeasonDetailsForContinueTracking(
                    seasonId = SEASON_1_ID,
                    seasonNumber = SEASON_1_NUMBER,
                    episodes = season1Episodes,
                ),
            )

            val result = awaitItem()
            result.shouldNotBeNull()
            result.currentSeasonNumber shouldBe SEASON_1_NUMBER
            result.episodes shouldHaveSize 2
            result.firstUnwatchedIndex shouldBe 0
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

        val _ = database.showMetadataQueries.upsert(
            show_id = Id(TEST_SHOW_ID),
            season_count = 2,
            episode_count = (SEASON_1_EPISODE_COUNT + SEASON_2_EPISODE_COUNT).toLong(),
            status = "Returning Series",
        )
    }
}
