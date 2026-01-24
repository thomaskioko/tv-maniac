package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse.Success
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.TraktId
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
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.i18n.testing.util.IgnoreIos
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.trakt.api.TraktCalendarRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarResponse
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
    private val fakeSeasonDetailsRepository = FakeSeasonDetailsRepository()
    private val fakeDatastoreRepository = FakeDatastoreRepository()
    private val fakeSeasonsRepository = FakeSeasonsRepository()
    private val fakeDateTimeProvider = FakeDateTimeProvider()
    private val fakeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val fakeRequestManagerRepository = FakeRequestManagerRepository()
    private val fakeCalendarDataSource = object : TraktCalendarRemoteDataSource {
        override suspend fun getMyShowsCalendar(startDate: String, days: Int) =
            Success(emptyList<TraktCalendarResponse>())
    }
    private val nextEpisodeDao by lazy { DefaultNextEpisodeDao(database, coroutineDispatcher) }
    private val episodesDao by lazy { DefaultEpisodesDao(database, coroutineDispatcher, fakeDateTimeProvider) }
    private val watchedEpisodeDao by lazy {
        DefaultWatchedEpisodeDao(
            database = database,
            dispatchers = coroutineDispatcher,
            dateTimeProvider = fakeDateTimeProvider,
        )
    }
    private val upcomingEpisodesStore by lazy {
        UpcomingEpisodesStore(
            calendarDataSource = fakeCalendarDataSource,
            episodesDao = episodesDao,
            requestManagerRepository = fakeRequestManagerRepository,
            dispatchers = coroutineDispatcher,
        )
    }
    private lateinit var episodeRepository: EpisodeRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        episodeRepository = DefaultEpisodeRepository(
            watchedEpisodeDao = watchedEpisodeDao,
            nextEpisodeDao = nextEpisodeDao,
            episodesDao = episodesDao,
            database = database,
            datastoreRepository = fakeDatastoreRepository,
            dispatchers = coroutineDispatcher,
            seasonsRepository = fakeSeasonsRepository,
            seasonDetailsRepository = fakeSeasonDetailsRepository,
            syncRepository = lazy { fakeSyncRepository },
            upcomingEpisodesStore = upcomingEpisodesStore,
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
    fun `should observe last watched episode`() = runTest {
        val flow = episodeRepository.observeLastWatchedEpisode(showTraktId = TEST_SHOW_ID)

        flow.test {
            val initialItem = awaitItem()
            initialItem.shouldBeNull()

            episodeRepository.markEpisodeAsWatched(
                showTraktId = TEST_SHOW_ID,
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
                showTraktId = TEST_SHOW_ID,
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
                showTraktId = TEST_SHOW_ID,
                episodeId = 203L,
                seasonNumber = SEASON_2_NUMBER,
                episodeNumber = 3L,
            )

            episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
                val result = awaitItem()
                result.shouldNotBeNull()
                result.currentSeasonNumber shouldBe SEASON_2_NUMBER
                result.episodes shouldHaveSize 4
            }
        }

    @Test
    fun `should return season 1 for continue tracking when user has no watch progress`() = runTest {
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
        }
    }

    @Test
    fun `should return season with future episodes when all previous seasons watched`() = runTest {
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

        episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
            val result = awaitItem()
            result.shouldBeNull()
        }
    }

    @Test
    fun `should return null for continue tracking when show not in library`() = runTest {
        val notInLibraryShowId = 999L
        episodeRepository.observeContinueTrackingEpisodes(notInLibraryShowId).test {
            val result = awaitItem()
            result.shouldBeNull()
        }
    }

    @Test
    fun `should emit continue tracking result when season details change`() = runTest {
        val initialEpisodes = MockData.createSeason1EpisodesForContinueTracking()
        fakeSeasonDetailsRepository.setSeasonsResult(
            createSeasonDetailsForContinueTracking(
                seasonId = SEASON_1_ID,
                seasonNumber = SEASON_1_NUMBER,
                episodes = initialEpisodes,
            ),
        )

        episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
            val result = awaitItem()
            result.shouldNotBeNull()
            result.currentSeasonNumber shouldBe SEASON_1_NUMBER
            result.episodes shouldHaveSize 2
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
            trakt_id = Id<TraktId>(TEST_SHOW_ID),
            tmdb_id = Id<TmdbId>(TEST_SHOW_ID),
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
            show_trakt_id = Id<TraktId>(TEST_SHOW_ID),
            season_number = SEASON_1_NUMBER,
            title = "Season 1",
            overview = "First season",
            episode_count = SEASON_1_EPISODE_COUNT.toLong(),
            image_url = "/season1.jpg",
        )

        val _ = database.seasonsQueries.upsert(
            id = Id(SEASON_2_ID),
            show_trakt_id = Id<TraktId>(TEST_SHOW_ID),
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
                show_trakt_id = Id<TraktId>(TEST_SHOW_ID),
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
