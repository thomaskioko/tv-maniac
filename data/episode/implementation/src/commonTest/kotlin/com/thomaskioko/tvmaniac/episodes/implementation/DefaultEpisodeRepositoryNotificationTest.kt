package com.thomaskioko.tvmaniac.episodes.implementation

import com.thomaskioko.tvmaniac.core.base.coroutines.FakeAppScopeLauncher
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.data.calendar.testing.FakeCalendarRemoteDataSource
import com.thomaskioko.tvmaniac.database.test.BaseDatabaseTest
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.db.Id
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultEpisodesDao
import com.thomaskioko.tvmaniac.episodes.implementation.dao.DefaultWatchedEpisodeDao
import com.thomaskioko.tvmaniac.episodes.testing.FakeWatchedEpisodeSyncRepository
import com.thomaskioko.tvmaniac.requestmanager.testing.FakeRequestManagerRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
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
internal class DefaultEpisodeRepositoryNotificationTest : BaseDatabaseTest() {

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
    private val notificationManager = FakeNotificationManager()

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
    fun `should cancel pending notification given episode marked watched`() = runTest {
        val repository = buildRepository()
        notificationManager.addPendingNotification(pendingNotification(id = EPISODE_ID, seasonNumber = 1))

        repository.markEpisodeAsWatched(
            showId = SHOW_ID,
            episodeId = EPISODE_ID,
            seasonNumber = 1L,
            episodeNumber = 1L,
        )

        notificationManager.getScheduledNotifications() shouldNotContainKey EPISODE_ID
    }

    @Test
    fun `should keep later season notification given earlier season marked watched`() = runTest {
        val repository = buildRepository()
        notificationManager.addPendingNotification(pendingNotification(id = 201, seasonNumber = 1))
        notificationManager.addPendingNotification(pendingNotification(id = 301, seasonNumber = 2))

        repository.markSeasonWatched(showId = SHOW_ID, seasonNumber = 1L)

        val scheduled = notificationManager.getScheduledNotifications()
        scheduled shouldNotContainKey 201L
        scheduled shouldContainKey 301L
    }

    @Test
    fun `should cancel previous season notifications given season and previous marked watched`() = runTest {
        val repository = buildRepository()
        notificationManager.addPendingNotification(pendingNotification(id = 201, seasonNumber = 1))
        notificationManager.addPendingNotification(pendingNotification(id = 301, seasonNumber = 2))
        notificationManager.addPendingNotification(pendingNotification(id = 401, seasonNumber = 3))

        repository.markSeasonAndPreviousSeasonsWatched(showId = SHOW_ID, seasonNumber = 2L)

        val scheduled = notificationManager.getScheduledNotifications()
        scheduled shouldNotContainKey 201L
        scheduled shouldNotContainKey 301L
        scheduled shouldContainKey 401L
    }

    private fun TestScope.buildRepository(): DefaultEpisodeRepository {
        val watchedEpisodeDao = DefaultWatchedEpisodeDao(database, showIdResolver, dispatchers, fakeDateTimeProvider)
        val episodesDao = DefaultEpisodesDao(database, showIdResolver, dispatchers, fakeDateTimeProvider)
        val upcomingEpisodesStore = UpcomingEpisodesStore(
            activeSource = { FakeCalendarRemoteDataSource() },
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
            syncObserver = FakeSyncObserver(),
            notificationManager = notificationManager,
        )
    }

    private fun pendingNotification(id: Long, seasonNumber: Long) = EpisodeNotification(
        id = id,
        showId = SHOW_ID,
        seasonId = seasonNumber,
        showName = "Test Show",
        episodeTitle = "Episode",
        seasonNumber = seasonNumber,
        episodeNumber = 1,
        imageUrl = null,
        scheduledTime = 2_000_000L,
        message = "message",
    )

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
