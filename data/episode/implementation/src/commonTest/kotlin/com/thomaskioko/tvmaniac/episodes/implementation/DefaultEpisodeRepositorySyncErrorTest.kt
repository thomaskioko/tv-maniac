package com.thomaskioko.tvmaniac.episodes.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.networkutil.api.model.ApiResponse
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultEpisodesDao
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultWatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.trakt.api.TraktCalendarRemoteDataSource
import com.thomaskioko.tvmaniac.trakt.api.model.TraktCalendarResponse
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
internal class DefaultEpisodeRepositorySyncErrorTest : BaseDatabaseTest() {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val fakeDateTimeProvider = FakeDateTimeProvider()
    private val datastoreRepository = FakeDatastoreRepository()
    private val syncRepository = FakeWatchedEpisodeSyncRepository()
    private val requestManagerRepository = FakeRequestManagerRepository()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        seedShow()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        closeDb()
    }

    @Test
    fun `should publish MarkWatchedFailed when syncPendingEpisodes throws on mark watched`() = runTest {
        val syncObserver = FakeSyncObserver()
        val repository = buildRepository(syncObserver)
        syncRepository.setPendingEpisodesError(RuntimeException("network down"))

        syncObserver.errors.test {
            repository.markEpisodeAsWatched(
                showId = SHOW_ID,
                episodeId = EPISODE_ID,
                seasonNumber = 1L,
                episodeNumber = 1L,
            )

            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.MarkWatchedFailed>()
            event.showId shouldBe SHOW_ID
            event.cause.message shouldBe "network down"
        }
    }

    @Test
    fun `should publish MarkUnwatchedFailed when syncPendingEpisodes throws on mark unwatched`() = runTest {
        val syncObserver = FakeSyncObserver()
        val repository = buildRepository(syncObserver)
        syncRepository.setPendingEpisodesError(RuntimeException("network down"))

        syncObserver.errors.test {
            repository.markEpisodeAsUnwatched(showId = SHOW_ID, episodeId = EPISODE_ID)

            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.MarkUnwatchedFailed>()
            event.showId shouldBe SHOW_ID
        }
    }

    @Test
    fun `should publish BatchMarkFailed when syncPendingEpisodes throws on mark season watched`() = runTest {
        val syncObserver = FakeSyncObserver()
        val repository = buildRepository(syncObserver)
        syncRepository.setPendingEpisodesError(RuntimeException("network down"))

        syncObserver.errors.test {
            repository.markSeasonWatched(showId = SHOW_ID, seasonNumber = 1L)

            val event = awaitItem()
            event.shouldBeInstanceOf<SyncError.BatchMarkFailed>()
            event.showId shouldBe SHOW_ID
        }
    }

    @Test
    fun `should not emit when syncPendingEpisodes succeeds`() = runTest {
        val syncObserver = FakeSyncObserver()
        val repository = buildRepository(syncObserver)
        syncRepository.setPendingEpisodesError(null)

        syncObserver.errors.test {
            repository.markEpisodeAsWatched(
                showId = SHOW_ID,
                episodeId = EPISODE_ID,
                seasonNumber = 1L,
                episodeNumber = 1L,
            )
            expectNoEvents()
        }
    }

    private fun TestScope.buildRepository(
        syncObserver: FakeSyncObserver,
    ): DefaultEpisodeRepository {
        val watchedEpisodeDao = DefaultWatchedEpisodeDao(database, showIdResolver, dispatchers, fakeDateTimeProvider)
        val episodesDao = DefaultEpisodesDao(database, showIdResolver, dispatchers, fakeDateTimeProvider)
        val upcomingEpisodesStore = UpcomingEpisodesStore(
            calendarDataSource = NoOpCalendarDataSource,
            episodesDao = episodesDao,
            requestManagerRepository = requestManagerRepository,
            dispatchers = dispatchers,
        )
        return DefaultEpisodeRepository(
            watchedEpisodeDao = watchedEpisodeDao,
            datastoreRepository = datastoreRepository,
            syncRepository = syncRepository,
            episodesDao = episodesDao,
            dispatchers = dispatchers,
            upcomingEpisodesStore = upcomingEpisodesStore,
            appScopeLauncher = FakeAppScopeLauncher(scope = this),
            syncObserver = syncObserver,
        )
    }

    private fun seedShow() {
        database.tvShowQueries.upsert(
            tmdb_id = Id(SHOW_ID),
            name = "Test Show",
            overview = "",
            language = "en",
            year = "2024",
            ratings = 8.0,
            vote_count = 100,
            genres = listOf("Drama"),
            status = "Returning Series",
            episode_numbers = null,
            season_numbers = null,
            poster_path = null,
            backdrop_path = null,
        )
        val showId = showIdForTraktId(SHOW_ID)
        database.seasonsQueries.upsert(
            id = Id(11L),
            show_id = showId,
            season_number = 1L,
            title = "Season 1",
            overview = null,
            episode_count = 2L,
            image_url = null,
        )
        database.episodesQueries.upsert(
            id = Id(EPISODE_ID),
            season_id = Id(11L),
            show_id = showId,
            title = "Pilot",
            overview = "",
            episode_number = 1L,
            runtime = 45L,
            image_url = null,
            ratings = 8.0,
            vote_count = 100L,
            first_aired = null,
        )
    }

    private companion object {
        private const val SHOW_ID = 1L
        private const val EPISODE_ID = 101L
    }
}

private object NoOpCalendarDataSource : TraktCalendarRemoteDataSource {
    override suspend fun getMyShowsCalendar(
        startDate: String,
        days: Int,
    ): ApiResponse<List<TraktCalendarResponse>> = ApiResponse.Success(emptyList())
}
