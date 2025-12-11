package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.db.SearchWatchlist
import com.thomaskioko.tvmaniac.db.Show_metadata
import com.thomaskioko.tvmaniac.db.TmdbId
import com.thomaskioko.tvmaniac.db.Watchlists
import com.thomaskioko.tvmaniac.episodes.api.EpisodeRepository
import com.thomaskioko.tvmaniac.episodes.api.model.EpisodeWatchParams
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_0_EPISODE_COUNT
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_0_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_0_NUMBER
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_1_EPISODE_COUNT
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_1_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_1_NUMBER
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_2_EPISODE_COUNT
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_2_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.SEASON_2_NUMBER
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.TEST_SHOW_ID
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.TEST_SHOW_NAME
import com.thomaskioko.tvmaniac.episodes.implementation.MockData.TEST_SHOW_OVERVIEW
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.shows.api.WatchlistDao
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Instant

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
    private val nextEpisodeDao = DefaultNextEpisodeDao(database, coroutineDispatcher)
    private val watchedEpisodeDao = DefaultWatchedEpisodeDao(
        database = database,
        nextEpisodeDao = nextEpisodeDao,
        dispatchers = coroutineDispatcher,
    )
    private val watchAnalyticsHelper = WatchAnalyticsHelper(
        database = database,
        datastoreRepository = fakeDatastoreRepository,
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
            watchAnalyticsHelper = watchAnalyticsHelper,
            dateTimeProvider = fakeDateTimeProvider,
        )

        insertTestData()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should observe next episode for show with no watched episodes`() = runTest {
        episodeRepository.observeNextEpisodeForShow(showId = TEST_SHOW_ID).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 101L
            nextEpisode.seasonNumber shouldBe 1L
            nextEpisode.episodeNumber shouldBe 1L
            nextEpisode.episodeName shouldBe "Episode 1"
        }
    }

    @Test
    fun `should observe next episode after marking episodes watched`() = runTest {
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        )

        episodeRepository.observeNextEpisodeForShow(showId = TEST_SHOW_ID).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 102L
            nextEpisode.seasonNumber shouldBe 1L
            nextEpisode.episodeNumber shouldBe 2L
            nextEpisode.episodeName shouldBe "Episode 2"
        }
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

        episodeRepository.observeNextEpisodeForShow(showId = TEST_SHOW_ID).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 201L
            nextEpisode.seasonNumber shouldBe 2L
            nextEpisode.episodeNumber shouldBe 1L
            nextEpisode.episodeName shouldBe "Episode 1"
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

        episodeRepository.observeNextEpisodeForShow(showId = TEST_SHOW_ID).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldBeNull()
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
    fun `should mark episode as watched`() = runTest {
        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem().shouldBeEmpty()

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 101L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 1L,
            )

            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize 1
            watchedEpisodes.first().show_id shouldBe Id(TEST_SHOW_ID)
            watchedEpisodes.first().episode_id shouldBe Id(101L)
            watchedEpisodes.first().season_number shouldBe 1L
            watchedEpisodes.first().episode_number shouldBe 1L
        }
    }

    @Test
    fun `should mark episode as unwatched`() = runTest {
        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem().shouldBeEmpty()

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 101L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 1L,
            )

            awaitItem() shouldHaveSize 1

            episodeRepository.markEpisodeAsUnwatched(
                showId = TEST_SHOW_ID,
                episodeId = 101L,
            )

            val watchedEpisodes = awaitItem()
            watchedEpisodes.shouldBeEmpty()
        }
    }

    @Test
    fun `should get last watched episode`() = runTest {
        episodeRepository.getLastWatchedEpisode(showId = TEST_SHOW_ID).shouldBeNull()

        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        )

        val lastWatched = episodeRepository.getLastWatchedEpisode(showId = TEST_SHOW_ID)
        lastWatched.shouldNotBeNull()
        lastWatched.episode_id shouldBe Id(101L)

        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
        )

        val newLastWatched = episodeRepository.getLastWatchedEpisode(TEST_SHOW_ID)
        newLastWatched.shouldNotBeNull()
        newLastWatched.episode_id shouldBe Id(103L)
        newLastWatched.season_number shouldBe 1L
        newLastWatched.episode_number shouldBe 3L
    }

    @Test
    fun `should check if episode is watched`() = runTest {
        episodeRepository.isEpisodeWatched(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        ) shouldBe false

        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        )

        episodeRepository.isEpisodeWatched(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        ) shouldBe true

        episodeRepository.isEpisodeWatched(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 2L,
        ) shouldBe false
    }

    @Test
    fun `should track watch progress correctly`() = runTest {
        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem().shouldBeEmpty()

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 101L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 1L,
            )

            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize 1
            watchedEpisodes.first().episode_id shouldBe Id(101L)
            watchedEpisodes.first().season_number shouldBe 1L
            watchedEpisodes.first().episode_number shouldBe 1L

            val lastWatched = episodeRepository.getLastWatchedEpisode(showId = TEST_SHOW_ID)
            lastWatched.shouldNotBeNull()
            lastWatched.episode_id shouldBe Id(101L)
            lastWatched.season_number shouldBe 1L
            lastWatched.episode_number shouldBe 1L
        }
    }

    @Test
    fun `should clear watch history for show`() = runTest {
        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem().shouldBeEmpty()

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 101L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 1L,
            )
            awaitItem() shouldHaveSize 1

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 102L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 2L,
            )
            awaitItem() shouldHaveSize 2

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 103L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 3L,
            )
            awaitItem() shouldHaveSize 3

            episodeRepository.clearCachedWatchHistoryForShow(showId = TEST_SHOW_ID)

            val watchedEpisodes = awaitItem()
            watchedEpisodes.shouldBeEmpty()
        }

        episodeRepository.observeNextEpisodeForShow(showId = TEST_SHOW_ID).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 101L
            nextEpisode.seasonNumber shouldBe 1L
            nextEpisode.episodeNumber shouldBe 1L
        }
    }

    @Test
    fun `should handle cross-season episode progression`() = runTest {
        episodeRepository.observeNextEpisodeForShow(showId = TEST_SHOW_ID).test {
            var nextEpisode = awaitItem()
            nextEpisode?.episodeId shouldBe 101L

            repeat(6) { episodeIndex ->
                val episodeNumber = episodeIndex + 1
                val episodeId = 100L + episodeNumber
                episodeRepository.markEpisodeAsWatched(
                    TEST_SHOW_ID,
                    episodeId,
                    SEASON_1_NUMBER,
                    episodeNumber.toLong(),
                )

                nextEpisode = awaitItem()
                nextEpisode?.episodeNumber shouldBe (episodeNumber + 1).toLong()
            }

            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = 107L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 7L,
            )

            nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()

            nextEpisode.episodeId shouldBe 201L
            nextEpisode.seasonNumber shouldBe 2L
            nextEpisode.episodeNumber shouldBe 1L
        }
    }

    @Test
    fun `should handle partial watch progress`() = runTest {
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
        )

        episodeRepository.observeNextEpisodeForShow(showId = TEST_SHOW_ID).test {
            val nextEpisode = awaitItem()
            nextEpisode.shouldNotBeNull()
            nextEpisode.episodeId shouldBe 104L
            nextEpisode.episodeNumber shouldBe 4L
        }

        episodeRepository.observeWatchProgress(showId = TEST_SHOW_ID).test {
            val progress = awaitItem()
            progress.totalEpisodesWatched shouldBe 1
            progress.lastSeasonWatched shouldBe 1L
            progress.lastEpisodeWatched shouldBe 3L
        }
    }

    @Test
    fun `should get comprehensive watch progress context`() = runTest {
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        )
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 102L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 2L,
        )
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
        )

        val context = episodeRepository.getWatchProgressContext(showId = TEST_SHOW_ID)

        context.showId shouldBe TEST_SHOW_ID
        context.watchedEpisodes shouldBe 3
        context.isWatchingOutOfOrder shouldBe false
        context.hasUnwatchedEarlierEpisodes shouldBe false
    }

    @Test
    fun `should detect out of order watching pattern`() = runTest {
        // Watch episode 3 first with an earlier timestamp
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
            watchedAt = Instant.fromEpochMilliseconds(1000),
        )

        // Then watch episode 1 (out of order) with a later timestamp
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
            watchedAt = Instant.fromEpochMilliseconds(2000),
        )

        val isOutOfOrder = episodeRepository.isWatchingOutOfOrder(showId = TEST_SHOW_ID)

        isOutOfOrder shouldBe true
    }

    @Test
    fun `should detect catching up watching pattern`() = runTest {
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        )
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
        )

        val hasEarlierUnwatched =
            episodeRepository.hasUnwatchedEarlierEpisodes(showId = TEST_SHOW_ID)

        hasEarlierUnwatched shouldBe true
    }

    @Test
    fun `should find earliest unwatched episode`() = runTest {
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        )
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
        )

        val earliestUnwatched =
            episodeRepository.findEarliestUnwatchedEpisode(showId = TEST_SHOW_ID)

        earliestUnwatched.shouldNotBeNull()
        earliestUnwatched.episodeId shouldBe 102L
        earliestUnwatched.seasonNumber shouldBe 1L
        earliestUnwatched.episodeNumber shouldBe 2L
        earliestUnwatched.episodeName shouldBe "Episode 2"
    }

    @Test
    fun `should return null when no unwatched episodes exist`() = runTest {
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

        val earliestUnwatched =
            episodeRepository.findEarliestUnwatchedEpisode(showId = TEST_SHOW_ID)
        earliestUnwatched.shouldBeNull()
    }

    @Test
    fun `should exclude specials when includeSpecials is false`() = runTest {
        insertSeason0Data()
        fakeDatastoreRepository.saveIncludeSpecials(false)

        val earliestUnwatched =
            episodeRepository.findEarliestUnwatchedEpisode(showId = TEST_SHOW_ID)

        earliestUnwatched.shouldNotBeNull()
        earliestUnwatched.seasonNumber shouldBe SEASON_1_NUMBER
        earliestUnwatched.episodeNumber shouldBe 1L
        earliestUnwatched.episodeId shouldBe 101L
    }

    @Test
    fun `should include specials when includeSpecials is true`() = runTest {
        insertSeason0Data()
        fakeDatastoreRepository.saveIncludeSpecials(true)

        val earliestUnwatched =
            episodeRepository.findEarliestUnwatchedEpisode(showId = TEST_SHOW_ID)

        earliestUnwatched.shouldNotBeNull()
        earliestUnwatched.seasonNumber shouldBe SEASON_0_NUMBER
        earliestUnwatched.episodeNumber shouldBe 1L
        earliestUnwatched.episodeId shouldBe 1L
    }

    @Test
    fun `should find cross-season earliest unwatched episode`() = runTest {
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

        val earliestUnwatched =
            episodeRepository.findEarliestUnwatchedEpisode(showId = TEST_SHOW_ID)

        earliestUnwatched.shouldNotBeNull()
        earliestUnwatched.seasonNumber shouldBe SEASON_2_NUMBER
        earliestUnwatched.episodeNumber shouldBe 1L
        earliestUnwatched.episodeId shouldBe 201L
    }

    @Test
    fun `should exclude future episodes from earliest unwatched`() = runTest {
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

        database.episodesQueries.upsert(
            id = Id(201L),
            season_id = Id(SEASON_2_ID),
            show_id = Id(TEST_SHOW_ID),
            title = "Future Episode 1",
            overview = "Future episode overview",
            episode_number = 1L,
            runtime = 45L,
            image_url = "/future1.jpg",
            vote_average = 0.0,
            vote_count = 0L,
            air_date = "2099-12-31",
            trakt_id = null,
        )

        val earliestUnwatched =
            episodeRepository.findEarliestUnwatchedEpisode(showId = TEST_SHOW_ID)

        if (earliestUnwatched != null) {
            earliestUnwatched.episodeId shouldBe 202L
        }
    }

    private fun insertSeason0Data() {
        database.seasonsQueries.upsert(
            id = Id(SEASON_0_ID),
            show_id = Id(TEST_SHOW_ID),
            season_number = SEASON_0_NUMBER,
            title = "Specials",
            overview = "Specials",
            episode_count = SEASON_0_EPISODE_COUNT.toLong(),
            image_url = "/specials.jpg",
        )

        repeat(SEASON_0_EPISODE_COUNT) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = episodeNumber.toLong()
            database.episodesQueries.upsert(
                id = Id(episodeId),
                season_id = Id(SEASON_0_ID),
                show_id = Id(TEST_SHOW_ID),
                title = "Special $episodeNumber",
                overview = "Special $episodeNumber overview",
                episode_number = episodeNumber.toLong(),
                runtime = 30L,
                image_url = "/special$episodeNumber.jpg",
                vote_average = 7.0,
                vote_count = 25L,
                air_date = "2022-12-0$episodeNumber",
                trakt_id = null,
            )
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
    fun `should handle linear watching pattern correctly`() = runTest {
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        )
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 102L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 2L,
        )
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
        )

        val isOutOfOrder = episodeRepository.isWatchingOutOfOrder(showId = TEST_SHOW_ID)
        val hasEarlierUnwatched =
            episodeRepository.hasUnwatchedEarlierEpisodes(showId = TEST_SHOW_ID)

        isOutOfOrder shouldBe false
        hasEarlierUnwatched shouldBe false
    }

    @Test
    fun `should calculate correct progress percentage`() = runTest {
        val totalEpisodes = SEASON_1_EPISODE_COUNT + SEASON_2_EPISODE_COUNT
        repeat(5) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            episodeRepository.markEpisodeAsWatched(
                showId = TEST_SHOW_ID,
                episodeId = episodeId,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = episodeNumber.toLong(),
            )
        }

        val context = episodeRepository.getWatchProgressContext(showId = TEST_SHOW_ID)
        context.progressPercentage.toInt() shouldBe (5 * 100 / totalEpisodes)
    }

    @Test
    fun `should handle empty watch history correctly`() = runTest {
        val totalEpisodes = SEASON_1_EPISODE_COUNT + SEASON_2_EPISODE_COUNT
        val context = episodeRepository.getWatchProgressContext(showId = TEST_SHOW_ID)

        context.showId shouldBe TEST_SHOW_ID
        context.totalEpisodes shouldBe totalEpisodes
        context.watchedEpisodes shouldBe 0
        context.lastWatchedSeasonNumber shouldBe null
        context.lastWatchedEpisodeNumber shouldBe null
        context.progressPercentage shouldBe 0.0f
        context.isWatchingOutOfOrder shouldBe false
        context.hasUnwatchedEarlierEpisodes shouldBe false
    }

    @Test
    fun `should mark season watched correctly`() = runTest {
        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem()

            episodeRepository.markSeasonWatched(
                showId = TEST_SHOW_ID,
                seasonNumber = SEASON_1_NUMBER,
            )

            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize SEASON_1_EPISODE_COUNT
            watchedEpisodes.all { it.season_number == SEASON_1_NUMBER } shouldBe true
        }
    }

    @Test
    fun `should get unwatched episodes in previous seasons`() = runTest {
        val unwatchedEpisodes = episodeRepository.getUnwatchedEpisodesInPreviousSeasons(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_2_NUMBER,
        )

        unwatchedEpisodes shouldHaveSize SEASON_1_EPISODE_COUNT
        unwatchedEpisodes.all { it.seasonNumber == SEASON_1_NUMBER } shouldBe true
    }

    @Test
    fun `should mark multiple episodes watched for previous seasons`() = runTest {
        val unwatchedEpisodes = episodeRepository.getUnwatchedEpisodesInPreviousSeasons(
            showId = TEST_SHOW_ID,
            seasonNumber = SEASON_2_NUMBER,
        )

        val episodesToMark = unwatchedEpisodes.map { unwatched ->
            EpisodeWatchParams(
                episodeId = unwatched.episodeId,
                seasonNumber = unwatched.seasonNumber,
                episodeNumber = unwatched.episodeNumber,
            )
        }

        episodeRepository.markMultipleEpisodesWatched(
            showId = TEST_SHOW_ID,
            episodes = episodesToMark,
        )
        episodeRepository.markSeasonWatched(showId = TEST_SHOW_ID, seasonNumber = SEASON_2_NUMBER)

        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize (SEASON_1_EPISODE_COUNT + SEASON_2_EPISODE_COUNT)
        }
    }

    @Test
    fun `should return active season for continue tracking when user has watch progress`() =
        runTest {
            fakeSeasonsRepository.setSeasonsResult(MockData.testShowSeasons)

            val season2Episodes =
                MockData.createSeason2EpisodesWithWatchedState(watchedEpisodeNumber = 3L)
            fakeSeasonDetailsRepository.setSeasonsResult(
                MockData.createSeasonDetailsForContinueTracking(
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
        fakeSeasonsRepository.setSeasonsResult(MockData.testShowSeasons)

        val season1Episodes = MockData.createSeason1EpisodesForContinueTracking()
        fakeSeasonDetailsRepository.setSeasonsResult(
            MockData.createSeasonDetailsForContinueTracking(
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
        fakeSeasonsRepository.setSeasonsResult(MockData.testShowSeasons)

        val futureEpisodes = MockData.createFutureEpisodesForSeason(
            seasonId = SEASON_1_ID,
            seasonNumber = SEASON_1_NUMBER,
            episodeCount = 3,
            daysUntilAir = 14,
        )
        fakeSeasonDetailsRepository.setSeasonsResult(
            MockData.createSeasonDetailsForContinueTracking(
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
        fakeSeasonsRepository.setSeasonsResult(MockData.testShowSeasons)

        val allWatchedEpisodes = MockData.createAllWatchedEpisodesForSeason(
            seasonId = SEASON_1_ID,
            seasonNumber = SEASON_1_NUMBER,
            episodeCount = 5,
        )
        fakeSeasonDetailsRepository.setSeasonsResult(
            MockData.createSeasonDetailsForContinueTracking(
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
        fakeSeasonsRepository.setSeasonsResult(MockData.testShowSeasons)

        episodeRepository.observeContinueTrackingEpisodes(TEST_SHOW_ID).test {
            val result = awaitItem()
            result.shouldBeNull()
        }
    }

    @Test
    fun `should mark episode and all previous episodes as watched`() = runTest {
        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem().shouldBeEmpty()

            episodeRepository.markEpisodeAndPreviousEpisodesWatched(
                showId = TEST_SHOW_ID,
                episodeId = 105L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 5L,
            )

            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize 5
            watchedEpisodes.map { it.episode_number }.sorted() shouldBe listOf(1L, 2L, 3L, 4L, 5L)
            watchedEpisodes.all { it.season_number == SEASON_1_NUMBER } shouldBe true
        }
    }

    @Test
    fun `should mark episode and previous episodes across seasons`() = runTest {
        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem().shouldBeEmpty()

            episodeRepository.markEpisodeAndPreviousEpisodesWatched(
                showId = TEST_SHOW_ID,
                episodeId = 202L,
                seasonNumber = SEASON_2_NUMBER,
                episodeNumber = 2L,
            )

            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize (SEASON_1_EPISODE_COUNT + 2)

            val season1Episodes = watchedEpisodes.filter { it.season_number == SEASON_1_NUMBER }
            season1Episodes shouldHaveSize SEASON_1_EPISODE_COUNT

            val season2Episodes = watchedEpisodes.filter { it.season_number == SEASON_2_NUMBER }
            season2Episodes shouldHaveSize 2
            season2Episodes.map { it.episode_number }.sorted() shouldBe listOf(1L, 2L)
        }
    }

    @Test
    fun `should mark season and all previous seasons as watched`() = runTest {
        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem().shouldBeEmpty()

            episodeRepository.markSeasonAndPreviousSeasonsWatched(
                showId = TEST_SHOW_ID,
                seasonNumber = SEASON_2_NUMBER,
            )

            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize (SEASON_1_EPISODE_COUNT + SEASON_2_EPISODE_COUNT)

            val season1Episodes = watchedEpisodes.filter { it.season_number == SEASON_1_NUMBER }
            season1Episodes shouldHaveSize SEASON_1_EPISODE_COUNT

            val season2Episodes = watchedEpisodes.filter { it.season_number == SEASON_2_NUMBER }
            season2Episodes shouldHaveSize SEASON_2_EPISODE_COUNT
        }
    }

    @Test
    fun `should mark only current season when no previous seasons exist`() = runTest {
        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem().shouldBeEmpty()

            episodeRepository.markSeasonAndPreviousSeasonsWatched(
                showId = TEST_SHOW_ID,
                seasonNumber = SEASON_1_NUMBER,
            )

            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize SEASON_1_EPISODE_COUNT
            watchedEpisodes.all { it.season_number == SEASON_1_NUMBER } shouldBe true
        }
    }

    @Test
    fun `should not duplicate already watched episodes when marking with history`() = runTest {
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 101L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 1L,
        )
        episodeRepository.markEpisodeAsWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
        )

        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            awaitItem() shouldHaveSize 2

            episodeRepository.markEpisodeAndPreviousEpisodesWatched(
                showId = TEST_SHOW_ID,
                episodeId = 105L,
                seasonNumber = SEASON_1_NUMBER,
                episodeNumber = 5L,
            )

            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize 5
            watchedEpisodes.map { it.episode_number }.sorted() shouldBe listOf(1L, 2L, 3L, 4L, 5L)
        }
    }

    @Test
    fun `should use same timestamp for all batch-marked episodes`() = runTest {
        val fixedTimestamp = 1000000L
        fakeDateTimeProvider.setCurrentTimeMillis(fixedTimestamp)

        episodeRepository.markEpisodeAndPreviousEpisodesWatched(
            showId = TEST_SHOW_ID,
            episodeId = 103L,
            seasonNumber = SEASON_1_NUMBER,
            episodeNumber = 3L,
        )

        episodeRepository.observeWatchedEpisodes(showId = TEST_SHOW_ID).test {
            val watchedEpisodes = awaitItem()
            watchedEpisodes shouldHaveSize 3
            watchedEpisodes.all { it.watched_at == fixedTimestamp } shouldBe true
        }
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

private class FakeWatchlistDao : WatchlistDao {
    private val isInLibraryFlow = MutableStateFlow(true)

    fun setIsInLibrary(inLibrary: Boolean) {
        isInLibraryFlow.value = inLibrary
    }

    override fun upsert(id: Long) {}
    override fun getShowsInWatchlist(): List<Watchlists> = emptyList()
    override fun updateSyncState(id: Id<TmdbId>) {}
    override fun observeShowsInWatchlist(): Flow<List<Watchlists>> = MutableStateFlow(emptyList())
    override fun observeWatchlistByQuery(query: String): Flow<List<SearchWatchlist>> =
        MutableStateFlow(emptyList())

    override fun observeUnSyncedWatchlist(): Flow<List<Id<TmdbId>>> = MutableStateFlow(emptyList())
    override fun delete(id: Long) {}
    override fun upsert(entity: Show_metadata) {}
    override suspend fun isShowInLibrary(showId: Long): Boolean = isInLibraryFlow.value
    override fun observeIsShowInLibrary(showId: Long): Flow<Boolean> = isInLibraryFlow
}
