package com.thomaskioko.tvmaniac.upnext.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.data.showdetails.testing.FakeShowDetailsRepository
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultNextEpisodeDao
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.followedshows.implementation.DefaultFollowedShowsDao
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
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

private fun LocalDate.toEpochMillis(): Long =
    atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultUpNextRepositoryTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val dateTimeProvider = FakeDateTimeProvider()
    private val requestManagerRepository = FakeRequestManagerRepository()
    private val seasonDetailsRepository = FakeSeasonDetailsRepository()
    private val showDetailsRepository = FakeShowDetailsRepository()
    private val watchedEpisodeSyncRepository = FakeWatchedEpisodeSyncRepository()
    private val datastoreRepository = FakeDatastoreRepository()

    private lateinit var nextEpisodeDao: DefaultNextEpisodeDao
    private lateinit var followedShowsDao: DefaultFollowedShowsDao
    private lateinit var repository: DefaultUpNextRepository

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        dateTimeProvider.setCurrentTimeMillis(NOW)

        nextEpisodeDao = DefaultNextEpisodeDao(database, dispatchers, dateTimeProvider)
        followedShowsDao = DefaultFollowedShowsDao(database, dispatchers)

        repository = DefaultUpNextRepository(
            nextEpisodeDao = nextEpisodeDao,
            datastoreRepository = datastoreRepository,
            followedShowsDao = followedShowsDao,
            showDetailsRepository = showDetailsRepository,
            seasonDetailsRepository = seasonDetailsRepository,
            watchedEpisodeSyncRepository = watchedEpisodeSyncRepository,
            requestManagerRepository = requestManagerRepository,
            logger = FakeLogger(),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should expose next episodes for followed shows from live source of truth`() = runTest {
        insertShow(id = 1L, name = "Severance")
        insertFollowedShow(showId = 1L, pendingAction = "NOTHING")
        insertSeason(showId = 1L, seasonNumber = 1)
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 1, title = "Good News About Hell")
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 2, title = "Half Loop")

        repository.observeNextEpisodesForWatchlist().test {
            val items = awaitItem()
            items.size shouldBe 1
            items[0].showName shouldBe "Severance"
            items[0].seasonNumber shouldBe 1L
            items[0].episodeNumber shouldBe 1L
        }
    }

    @Test
    fun `should advance next episode when watched_episodes is updated`() = runTest {
        insertShow(id = 1L, name = "Severance")
        insertFollowedShow(showId = 1L, pendingAction = "NOTHING")
        insertSeason(showId = 1L, seasonNumber = 1)
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 1, title = "Good News About Hell")
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 2, title = "Half Loop")

        repository.observeNextEpisodesForWatchlist().test {
            awaitItem()[0].episodeNumber shouldBe 1L

            insertWatchedEpisode(showId = 1L, episodeId = 1001L, seasonNumber = 1, episodeNumber = 1)

            awaitItem()[0].episodeNumber shouldBe 2L
        }
    }

    @Test
    fun `should hide caught-up shows whose next episode has not aired`() = runTest {
        val realNow = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val pastMillis = realNow - 86_400_000L
        val futureMillis = realNow + 7L * 86_400_000L
        dateTimeProvider.setCurrentTimeMillis(realNow)

        insertShow(id = 1L, name = "Caught Up Show")
        insertFollowedShow(showId = 1L, pendingAction = "NOTHING")
        insertSeason(showId = 1L, seasonNumber = 1, episodeCount = 2)
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 1, title = "Pilot", firstAired = pastMillis)
        insertEpisode(showId = 1L, seasonId = 101L, episodeNumber = 2, title = "Future", firstAired = futureMillis)

        insertWatchedEpisode(showId = 1L, episodeId = 1001L, seasonNumber = 1, episodeNumber = 1)

        repository.observeNextEpisodesForWatchlist().test {
            awaitItem().size shouldBe 0
        }
    }

    @Test
    fun `should exclude shows pending delete from followed count`() = runTest {
        insertShow(id = 1L, name = "Show A")
        insertShow(id = 2L, name = "Show B")
        insertShow(id = 3L, name = "Show C")
        insertFollowedShow(showId = 1L, pendingAction = "UPLOAD")
        insertFollowedShow(showId = 2L, pendingAction = "NOTHING")
        insertFollowedShow(showId = 3L, pendingAction = "DELETE")

        repository.observeFollowedShowsCount().test {
            awaitItem() shouldBe 2
        }
    }

    @Test
    fun `should sync season metadata and watched episodes for each followed show given sync invalid`() = runTest {
        insertShow(id = 1L, name = "Show A")
        insertShow(id = 2L, name = "Show B")
        insertFollowedShow(showId = 1L, pendingAction = "NOTHING")
        insertFollowedShow(showId = 2L, pendingAction = "NOTHING")
        requestManagerRepository.requestValid = false

        repository.fetchUpNextEpisodes(forceRefresh = false)

        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(1L, 2L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldBe listOf(1L, 2L)
        requestManagerRepository.upsertCalled shouldBe true
    }

    @Test
    fun `should skip metadata sync when request still valid and no force refresh`() = runTest {
        insertShow(id = 1L, name = "Show A")
        insertFollowedShow(showId = 1L, pendingAction = "NOTHING")
        requestManagerRepository.requestValid = true

        repository.fetchUpNextEpisodes(forceRefresh = false)

        seasonDetailsRepository.getSyncedShowIds().size shouldBe 0
        watchedEpisodeSyncRepository.getSyncedShowIds().size shouldBe 0
        requestManagerRepository.upsertCalled shouldBe false
    }

    @Test
    fun `should force metadata sync when force refresh given valid request`() = runTest {
        insertShow(id = 1L, name = "Show A")
        insertFollowedShow(showId = 1L, pendingAction = "NOTHING")
        requestManagerRepository.requestValid = true

        repository.fetchUpNextEpisodes(forceRefresh = true)

        seasonDetailsRepository.getSyncedShowIds() shouldBe listOf(1L)
        watchedEpisodeSyncRepository.getSyncedShowIds() shouldBe listOf(1L)
        watchedEpisodeSyncRepository.wasForceRefreshUsed() shouldBe true
        requestManagerRepository.upsertCalled shouldBe true
    }

    @Test
    fun `should no-op when no followed shows exist`() = runTest {
        requestManagerRepository.requestValid = false

        repository.fetchUpNextEpisodes(forceRefresh = false)

        seasonDetailsRepository.getSyncedShowIds().size shouldBe 0
        watchedEpisodeSyncRepository.getSyncedShowIds().size shouldBe 0
        requestManagerRepository.upsertCalled shouldBe false
    }

    private fun insertShow(id: Long, name: String) {
        database.tvShowQueries.upsert(
            trakt_id = Id(id),
            tmdb_id = Id(id),
            name = name,
            overview = "Overview for $name",
            language = "en",
            year = "2024-01-01",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = "/$id.jpg",
            backdrop_path = "/$id-back.jpg",
        )
    }

    private fun insertFollowedShow(
        showId: Long,
        followedAt: Long = NOW - 10_000,
        pendingAction: String = "NOTHING",
    ) {
        database.followedShowsQueries.upsert(
            id = null,
            traktId = Id(showId),
            tmdbId = Id(showId),
            followedAt = followedAt,
            pendingAction = pendingAction,
        )
    }

    private fun insertSeason(showId: Long, seasonNumber: Long, episodeCount: Long = 10) {
        database.seasonsQueries.upsert(
            id = Id(showId * 100 + seasonNumber),
            show_trakt_id = Id(showId),
            season_number = seasonNumber,
            episode_count = episodeCount,
            title = "Season $seasonNumber",
            overview = null,
            image_url = null,
        )
    }

    private fun insertEpisode(
        showId: Long,
        seasonId: Long,
        episodeNumber: Long,
        title: String,
        firstAired: Long = NOW - 86_400_000L,
    ) {
        database.episodesQueries.upsert(
            id = Id(showId * 1000 + episodeNumber),
            season_id = Id(seasonId),
            show_trakt_id = Id(showId),
            title = title,
            overview = "Overview for $title",
            ratings = 8.0,
            vote_count = 100,
            runtime = 45,
            episode_number = episodeNumber,
            image_url = null,
            trakt_id = showId * 1000 + episodeNumber,
            first_aired = firstAired,
        )
    }

    private fun insertWatchedEpisode(
        showId: Long,
        episodeId: Long,
        seasonNumber: Long,
        episodeNumber: Long,
    ) {
        database.watchedEpisodesQueries.upsert(
            show_trakt_id = Id(showId),
            episode_id = Id(episodeId),
            season_number = seasonNumber,
            episode_number = episodeNumber,
            watched_at = NOW,
            pending_action = "NOTHING",
        )
    }

    private companion object {
        private val NOW = LocalDate(2025, 6, 15).toEpochMillis()
    }
}
