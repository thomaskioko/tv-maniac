package com.thomaskioko.tvmaniac.domain.notifications

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

class NotificationTasksInitializerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val initializerScope = CoroutineScope(testDispatcher + Job())
    private val scheduler = FakeBackgroundTaskScheduler()
    private val notificationManager = FakeNotificationManager()
    private val datastoreRepository = FakeDatastoreRepository()
    private val accountManager = FakeAccountManager()

    @AfterTest
    fun tearDown() {
        initializerScope.cancel()
    }

    private fun startInitializer() {
        NotificationTasksInitializer(
            scheduler = scheduler,
            notificationManager = lazyOf(notificationManager),
            datastoreRepository = lazyOf(datastoreRepository),
            accountManager = lazyOf(accountManager),
            logger = FakeLogger(),
            coroutineScope = initializerScope,
        ).init()
    }

    @Test
    fun `should schedule worker given connected and notifications enabled`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setEpisodeNotificationsEnabled(true)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().single().id shouldBe EpisodeNotificationWorker.WORKER_NAME
    }

    @Test
    fun `should schedule worker given background sync disabled`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setEpisodeNotificationsEnabled(true)
        datastoreRepository.setBackgroundSyncEnabled(false)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests().single().id shouldBe EpisodeNotificationWorker.WORKER_NAME
    }

    @Test
    fun `should schedule worker without network constraint given notifications enabled`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setEpisodeNotificationsEnabled(true)

        startInitializer()
        testScheduler.advanceUntilIdle()

        val request = scheduler.getScheduledRequests().single()
        request.constraints.requiresNetwork shouldBe false
        request.longRunning shouldBe false
    }

    @Test
    fun `should cancel worker and pending notifications given notifications disabled`() = runTest(testDispatcher) {
        accountManager.setActiveProvider(SyncProviderSource.TRAKT)
        datastoreRepository.setEpisodeNotificationsEnabled(true)

        startInitializer()
        testScheduler.advanceUntilIdle()
        notificationManager.addPendingNotification(pendingNotification())

        datastoreRepository.setEpisodeNotificationsEnabled(false)
        testScheduler.advanceUntilIdle()

        scheduler.getCancelledIds() shouldBe listOf(EpisodeNotificationWorker.WORKER_NAME)
        notificationManager.getScheduledNotifications().shouldBeEmpty()
    }

    @Test
    fun `should not schedule worker given account is disconnected`() = runTest(testDispatcher) {
        datastoreRepository.setEpisodeNotificationsEnabled(true)

        startInitializer()
        testScheduler.advanceUntilIdle()

        scheduler.getScheduledRequests() shouldBe emptyList()
    }

    private fun pendingNotification() = EpisodeNotification(
        id = 101,
        showId = 1,
        seasonId = 10,
        showName = "Test Show",
        episodeTitle = "Episode 1",
        seasonNumber = 1,
        episodeNumber = 1,
        imageUrl = null,
        scheduledTime = 2_000_000L,
        message = "message",
    )
}
