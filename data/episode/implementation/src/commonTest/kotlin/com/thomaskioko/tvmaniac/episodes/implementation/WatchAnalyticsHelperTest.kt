package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.floats.shouldBeWithinPercentageOf
import io.kotest.matchers.nulls.shouldBeNull
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
internal class WatchAnalyticsHelperTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val coroutineDispatcher = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private lateinit var watchAnalyticsHelper: WatchAnalyticsHelper
    private lateinit var fakeDatastoreRepository: FakeDatastoreRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        fakeDatastoreRepository = FakeDatastoreRepository()

        watchAnalyticsHelper = WatchAnalyticsHelper(
            database = database,
            datastoreRepository = fakeDatastoreRepository,
            dispatchers = coroutineDispatcher,
        )

        insertTestData()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    private fun insertTestData() {
        database.tvShowQueries.upsert(
            id = Id(1),
            name = "Test Show",
            overview = "Test overview",
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
            poster_path = "/test.jpg",
            backdrop_path = "/backdrop.jpg",
        )

        database.seasonsQueries.upsert(
            id = Id(11L),
            show_id = Id(1L),
            season_number = 1L,
            title = "Season 1",
            overview = "First season",
            episode_count = 5L,
            image_url = "/season1.jpg",
        )

        database.seasonsQueries.upsert(
            id = Id(12L),
            show_id = Id(1L),
            season_number = 2L,
            title = "Season 2",
            overview = "Second season",
            episode_count = 5L,
            image_url = "/season2.jpg",
        )

        repeat(5) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 100L + episodeNumber
            database.episodesQueries.upsert(
                id = Id(episodeId),
                season_id = Id(11L),
                show_id = Id(1L),
                title = "S1E$episodeNumber",
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

        repeat(5) { episodeIndex ->
            val episodeNumber = episodeIndex + 1
            val episodeId = 200L + episodeNumber
            database.episodesQueries.upsert(
                id = Id(episodeId),
                season_id = Id(12L),
                show_id = Id(1L),
                title = "S2E$episodeNumber",
                overview = "Season 2 Episode $episodeNumber overview",
                episode_number = episodeNumber.toLong(),
                runtime = 45L,
                image_url = "/s2e$episodeNumber.jpg",
                vote_average = 9.0,
                vote_count = 75L,
                air_date = "2023-02-0$episodeNumber",
                trakt_id = null,
            )
        }

        database.watchlistQueries.upsert(
            id = Id(1L),
            created_at = Clock.System.now().toEpochMilliseconds(),
        )
    }

    private fun markEpisodeWatched(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
        watchedAtOffset: Long = 0L,
    ) {
        val watchedAt = Clock.System.now().toEpochMilliseconds() + watchedAtOffset
        database.watchedEpisodesQueries.upsert(
            show_id = Id(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = watchedAt,
        )
    }

    @Test
    fun `should return zero progress when no episodes watched`() = runTest {
        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.showId shouldBe 1L
        context.totalEpisodes shouldBe 10
        context.watchedEpisodes shouldBe 0
        context.progressPercentage shouldBe 0f
        context.lastWatchedSeasonNumber.shouldBeNull()
        context.lastWatchedEpisodeNumber.shouldBeNull()
        context.isWatchingOutOfOrder.shouldBeFalse()
        context.hasUnwatchedEarlierEpisodes.shouldBeFalse()
    }

    @Test
    fun `should calculate correct progress percentage`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L)
        markEpisodeWatched(1L, 102L, 1L, 2L, watchedAtOffset = 1000L)

        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.totalEpisodes shouldBe 10
        context.watchedEpisodes shouldBe 2
        context.progressPercentage.shouldBeWithinPercentageOf(20f, 0.1)
    }

    @Test
    fun `should return 100 percent progress when all episodes watched`() = runTest {
        repeat(5) { i ->
            markEpisodeWatched(1L, 101L + i, 1L, (i + 1).toLong(), watchedAtOffset = i * 1000L)
        }
        repeat(5) { i ->
            markEpisodeWatched(1L, 201L + i, 2L, (i + 1).toLong(), watchedAtOffset = (5 + i) * 1000L)
        }

        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.totalEpisodes shouldBe 10
        context.watchedEpisodes shouldBe 10
        context.progressPercentage.shouldBeWithinPercentageOf(100f, 0.1)
    }

    @Test
    fun `should track last watched episode in progress context`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L)
        markEpisodeWatched(1L, 102L, 1L, 2L, watchedAtOffset = 1000L)
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 2000L)

        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.lastWatchedSeasonNumber shouldBe 1
        context.lastWatchedEpisodeNumber shouldBe 3
    }

    @Test
    fun `should detect out of order watching in progress context`() = runTest {
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 0L)
        markEpisodeWatched(1L, 101L, 1L, 1L, watchedAtOffset = 1000L)

        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.isWatchingOutOfOrder.shouldBeTrue()
    }

    @Test
    fun `should detect skipped episodes in progress context`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L)
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 1000L)

        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.hasUnwatchedEarlierEpisodes.shouldBeTrue()
    }

    @Test
    fun `should return zero progress for nonexistent show`() = runTest {
        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 999L)

        context.showId shouldBe 999L
        context.totalEpisodes shouldBe 0
        context.watchedEpisodes shouldBe 0
        context.progressPercentage shouldBe 0f
    }

    @Test
    fun `should not detect out of order with no watched episodes`() = runTest {
        val result = watchAnalyticsHelper.isWatchingOutOfOrder(showId = 1L)

        result.shouldBeFalse()
    }

    @Test
    fun `should not detect out of order with only one watched episode`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L)

        val result = watchAnalyticsHelper.isWatchingOutOfOrder(showId = 1L)

        result.shouldBeFalse()
    }

    @Test
    fun `should not detect out of order when watching sequentially`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L, watchedAtOffset = 0L)
        markEpisodeWatched(1L, 102L, 1L, 2L, watchedAtOffset = 1000L)
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 2000L)

        val result = watchAnalyticsHelper.isWatchingOutOfOrder(showId = 1L)

        result.shouldBeFalse()
    }

    @Test
    fun `should detect out of order when watching later episodes first`() = runTest {
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 0L)
        markEpisodeWatched(1L, 101L, 1L, 1L, watchedAtOffset = 1000L)

        val result = watchAnalyticsHelper.isWatchingOutOfOrder(showId = 1L)

        result.shouldBeTrue()
    }

    @Test
    fun `should detect out of order when watching season 2 before season 1`() = runTest {
        markEpisodeWatched(1L, 201L, 2L, 1L, watchedAtOffset = 0L)
        markEpisodeWatched(1L, 101L, 1L, 1L, watchedAtOffset = 1000L)

        val result = watchAnalyticsHelper.isWatchingOutOfOrder(showId = 1L)

        result.shouldBeTrue()
    }

    @Test
    fun `should detect complex out of order watching pattern`() = runTest {
        markEpisodeWatched(1L, 102L, 1L, 2L, watchedAtOffset = 0L)
        markEpisodeWatched(1L, 104L, 1L, 4L, watchedAtOffset = 1000L)
        markEpisodeWatched(1L, 101L, 1L, 1L, watchedAtOffset = 2000L)
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 3000L)

        val result = watchAnalyticsHelper.isWatchingOutOfOrder(showId = 1L)

        result.shouldBeTrue()
    }

    @Test
    fun `should not detect earlier unwatched with no watched episodes`() = runTest {
        val result = watchAnalyticsHelper.hasUnwatchedEarlierEpisodes(showId = 1L)

        result.shouldBeFalse()
    }

    @Test
    fun `should not detect earlier unwatched when starting from episode 1`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L)

        val result = watchAnalyticsHelper.hasUnwatchedEarlierEpisodes(showId = 1L)

        result.shouldBeFalse()
    }

    @Test
    fun `should not detect earlier unwatched when watching consecutively`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L, watchedAtOffset = 0L)
        markEpisodeWatched(1L, 102L, 1L, 2L, watchedAtOffset = 1000L)
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 2000L)

        val result = watchAnalyticsHelper.hasUnwatchedEarlierEpisodes(showId = 1L)

        result.shouldBeFalse()
    }

    @Test
    fun `should detect earlier unwatched when skipping episodes`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L)
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 1000L)

        val result = watchAnalyticsHelper.hasUnwatchedEarlierEpisodes(showId = 1L)

        result.shouldBeTrue()
    }

    @Test
    fun `should detect earlier unwatched when skipping season 1`() = runTest {
        markEpisodeWatched(1L, 201L, 2L, 1L)

        val result = watchAnalyticsHelper.hasUnwatchedEarlierEpisodes(showId = 1L)

        result.shouldBeTrue()
    }

    @Test
    fun `should detect earlier unwatched when skipping multiple episodes`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L)
        markEpisodeWatched(1L, 105L, 1L, 5L, watchedAtOffset = 1000L)

        val result = watchAnalyticsHelper.hasUnwatchedEarlierEpisodes(showId = 1L)

        result.shouldBeTrue()
    }

    @Test
    fun `should not detect earlier unwatched when all earlier episodes are watched`() = runTest {
        markEpisodeWatched(1L, 101L, 1L, 1L, watchedAtOffset = 0L)
        markEpisodeWatched(1L, 102L, 1L, 2L, watchedAtOffset = 1000L)
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 2000L)
        markEpisodeWatched(1L, 104L, 1L, 4L, watchedAtOffset = 3000L)

        val result = watchAnalyticsHelper.hasUnwatchedEarlierEpisodes(showId = 1L)

        result.shouldBeFalse()
    }

    @Test
    fun `should detect unwatched episodes in previous season`() = runTest {
        repeat(4) { i ->
            markEpisodeWatched(1L, 101L + i, 1L, (i + 1).toLong(), watchedAtOffset = i * 1000L)
        }
        markEpisodeWatched(1L, 201L, 2L, 1L, watchedAtOffset = 5000L)

        val result = watchAnalyticsHelper.hasUnwatchedEarlierEpisodes(showId = 1L)

        result.shouldBeTrue()
    }

    @Test
    fun `should handle show with specials season correctly`() = runTest {
        database.seasonsQueries.upsert(
            id = Id(10L),
            show_id = Id(1L),
            season_number = 0L,
            title = "Specials",
            overview = "Special episodes",
            episode_count = 2L,
            image_url = "/specials.jpg",
        )

        database.episodesQueries.upsert(
            id = Id(1L),
            season_id = Id(10L),
            show_id = Id(1L),
            title = "Special 1",
            overview = "Special episode",
            episode_number = 1L,
            runtime = 45L,
            image_url = "/special1.jpg",
            vote_average = 8.0,
            vote_count = 30L,
            air_date = "2022-12-25",
            trakt_id = null,
        )

        fakeDatastoreRepository.saveIncludeSpecials(false)

        markEpisodeWatched(1L, 101L, 1L, 1L)

        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.totalEpisodes shouldBe 10
        context.hasUnwatchedEarlierEpisodes.shouldBeFalse()
    }

    @Test
    fun `should include specials when preference is enabled`() = runTest {
        database.seasonsQueries.upsert(
            id = Id(10L),
            show_id = Id(1L),
            season_number = 0L,
            title = "Specials",
            overview = "Special episodes",
            episode_count = 2L,
            image_url = "/specials.jpg",
        )

        database.episodesQueries.upsert(
            id = Id(1L),
            season_id = Id(10L),
            show_id = Id(1L),
            title = "Special 1",
            overview = "Special episode",
            episode_number = 1L,
            runtime = 45L,
            image_url = "/special1.jpg",
            vote_average = 8.0,
            vote_count = 30L,
            air_date = "2022-12-25",
            trakt_id = null,
        )

        fakeDatastoreRepository.saveIncludeSpecials(true)

        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.totalEpisodes shouldBe 11
    }

    @Test
    fun `should handle cross-season progress tracking`() = runTest {
        repeat(5) { i ->
            markEpisodeWatched(1L, 101L + i, 1L, (i + 1).toLong(), watchedAtOffset = i * 1000L)
        }
        markEpisodeWatched(1L, 201L, 2L, 1L, watchedAtOffset = 5000L)
        markEpisodeWatched(1L, 202L, 2L, 2L, watchedAtOffset = 6000L)

        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.watchedEpisodes shouldBe 7
        context.progressPercentage.shouldBeWithinPercentageOf(70f, 0.1)
        context.lastWatchedSeasonNumber shouldBe 2
        context.lastWatchedEpisodeNumber shouldBe 2
        context.isWatchingOutOfOrder.shouldBeFalse()
        context.hasUnwatchedEarlierEpisodes.shouldBeFalse()
    }

    @Test
    fun `should handle catching up pattern correctly`() = runTest {
        markEpisodeWatched(1L, 103L, 1L, 3L, watchedAtOffset = 0L)
        markEpisodeWatched(1L, 101L, 1L, 1L, watchedAtOffset = 1000L)
        markEpisodeWatched(1L, 102L, 1L, 2L, watchedAtOffset = 2000L)

        val context = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)

        context.isWatchingOutOfOrder.shouldBeTrue()
        context.hasUnwatchedEarlierEpisodes.shouldBeFalse()
    }

    @Test
    fun `should handle multiple shows independently`() = runTest {
        database.tvShowQueries.upsert(
            id = Id(2),
            name = "Test Show 2",
            overview = "Second test show",
            language = "en",
            first_air_date = "2023-03-01",
            vote_average = 7.5,
            vote_count = 80,
            popularity = 85.0,
            genre_ids = listOf(3, 4),
            status = "Ended",
            episode_numbers = null,
            last_air_date = null,
            season_numbers = null,
            poster_path = "/test2.jpg",
            backdrop_path = "/backdrop2.jpg",
        )

        database.seasonsQueries.upsert(
            id = Id(21L),
            show_id = Id(2L),
            season_number = 1L,
            title = "Season 1",
            overview = "Show 2 Season 1",
            episode_count = 3L,
            image_url = "/s2season1.jpg",
        )

        repeat(3) { i ->
            database.episodesQueries.upsert(
                id = Id(301L + i),
                season_id = Id(21L),
                show_id = Id(2L),
                title = "Show2 E${i + 1}",
                overview = "Show 2 Episode ${i + 1}",
                episode_number = (i + 1).toLong(),
                runtime = 30L,
                image_url = "/s2e${i + 1}.jpg",
                vote_average = 7.0,
                vote_count = 40L,
                air_date = "2023-03-0${i + 1}",
                trakt_id = null,
            )
        }

        markEpisodeWatched(1L, 101L, 1L, 1L)
        markEpisodeWatched(1L, 102L, 1L, 2L, watchedAtOffset = 1000L)

        markEpisodeWatched(2L, 303L, 1L, 3L)

        val context1 = watchAnalyticsHelper.getWatchProgressContext(showId = 1L)
        val context2 = watchAnalyticsHelper.getWatchProgressContext(showId = 2L)

        context1.watchedEpisodes shouldBe 2
        context1.hasUnwatchedEarlierEpisodes.shouldBeFalse()

        context2.watchedEpisodes shouldBe 1
        context2.hasUnwatchedEarlierEpisodes.shouldBeTrue()
    }
}
