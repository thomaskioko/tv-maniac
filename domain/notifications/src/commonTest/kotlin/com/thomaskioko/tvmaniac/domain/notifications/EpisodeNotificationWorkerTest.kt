package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.accountmanager.testing.FakeProviderFeatures
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.networkutil.testing.FakeApiRateLimiter
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.domain.notifications.interactor.RefreshUpcomingSeasonDetailsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.ScheduleEpisodeNotificationsInteractor
import com.thomaskioko.tvmaniac.domain.notifications.interactor.SyncCalendarInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ShowMetadataSyncHelper
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.seasondetails.testing.FakeSeasonDetailsRepository
import com.thomaskioko.tvmaniac.seasons.testing.FakeSeasonsRepository
import com.thomaskioko.tvmaniac.syncstate.testing.FakeSyncObserver
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours

class EpisodeNotificationWorkerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )
    private val datastoreRepository = FakeDatastoreRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val notificationManager = FakeNotificationManager()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val syncObserver = FakeSyncObserver()

    private val scheduleInteractor = ScheduleEpisodeNotificationsInteractor(
        datastoreRepository = datastoreRepository,
        episodeRepository = episodeRepository,
        notificationManager = notificationManager,
        localizer = TestLocalizer(),
        dateTimeProvider = dateTimeProvider,
        logger = FakeLogger(),
        dispatchers = dispatchers,
    )

    private val refreshInteractor = RefreshUpcomingSeasonDetailsInteractor(
        seasonsRepository = FakeSeasonsRepository(),
        seasonDetailsRepository = FakeSeasonDetailsRepository(),
        showMetadataSyncHelper = ShowMetadataSyncHelper(episodeRepository),
        apiRateLimiter = FakeApiRateLimiter(),
        dispatchers = dispatchers,
    )

    private val syncCalendarInteractor = SyncCalendarInteractor(
        episodeRepository = episodeRepository,
        dateTimeProvider = dateTimeProvider,
        activeProviderFeatures = { FakeProviderFeatures(supportsCalendar = true) },
        logger = FakeLogger(),
        dispatchers = dispatchers,
    )

    private val worker = EpisodeNotificationWorker(
        syncCalendarInteractor = lazyOf(syncCalendarInteractor),
        refreshInteractor = lazyOf(refreshInteractor),
        scheduleInteractor = lazyOf(scheduleInteractor),
        syncObserver = syncObserver,
        logger = FakeLogger(),
    )

    @Test
    fun `should schedule notifications given calendar sync fails`() = runTest(testDispatcher) {
        val currentTime = 1_000_000L
        dateTimeProvider.setCurrentTimeMillis(currentTime)
        datastoreRepository.setEpisodeNotificationsEnabled(true)
        episodeRepository.setUpcomingEpisodes(listOf(upcomingEpisode(episodeId = 201, firstAired = currentTime + 1.hours.inWholeMilliseconds)))
        episodeRepository.setSyncUpcomingEpisodesBehavior { error("calendar backend down") }

        worker.doWork() shouldBe WorkerResult.Success

        notificationManager.getScheduledNotifications() shouldContainKey 201L
    }

    @Test
    fun `should arm notifications from cached episodes given sync has not completed`() = runTest(testDispatcher) {
        val currentTime = 1_000_000L
        dateTimeProvider.setCurrentTimeMillis(currentTime)
        datastoreRepository.setEpisodeNotificationsEnabled(true)
        episodeRepository.setUpcomingEpisodes(listOf(upcomingEpisode(episodeId = 101, firstAired = currentTime + 1.hours.inWholeMilliseconds)))
        val syncGate = CompletableDeferred<Unit>()
        episodeRepository.setSyncUpcomingEpisodesBehavior { syncGate.await() }

        val result = async { worker.doWork() }
        testScheduler.advanceUntilIdle()

        result.isCompleted shouldBe false
        notificationManager.getScheduledNotifications() shouldContainKey 101L

        syncGate.complete(Unit)
        result.await() shouldBe WorkerResult.Success
    }

    private fun upcomingEpisode(episodeId: Long, firstAired: Long) = UpcomingEpisode(
        episodeId = episodeId,
        seasonId = 10,
        showId = 1,
        episodeNumber = 1,
        seasonNumber = 1,
        title = "Episode 1",
        overview = null,
        runtime = null,
        imageUrl = null,
        firstAired = firstAired,
        showName = "Test Show",
        showPoster = null,
    )
}
