package com.thomaskioko.tvmaniac.domain.notifications.interactor

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.notifications.api.EpisodeNotification
import com.thomaskioko.tvmaniac.core.notifications.testing.FakeNotificationManager
import com.thomaskioko.tvmaniac.core.view.InvokeStarted
import com.thomaskioko.tvmaniac.core.view.InvokeSuccess
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.episodes.api.model.UpcomingEpisode
import com.thomaskioko.tvmaniac.episodes.testing.FakeEpisodeRepository
import com.thomaskioko.tvmaniac.util.testing.FakeDateTimeProvider
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ScheduleEpisodeNotificationsInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val datastoreRepository = FakeDatastoreRepository()
    private val episodeRepository = FakeEpisodeRepository()
    private val notificationManager = FakeNotificationManager()
    private val dateTimeProvider = FakeDateTimeProvider()
    private val logger = FakeLogger()

    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )

    private val interactor = ScheduleEpisodeNotificationsInteractor(
        datastoreRepository = datastoreRepository,
        episodeRepository = episodeRepository,
        notificationManager = notificationManager,
        dateTimeProvider = dateTimeProvider,
        logger = logger,
        dispatchers = dispatchers,
    )

    @Test
    fun `should preserve existing alarm given notification time already passed`() = runTest(testDispatcher) {
        val currentTime = 1_000_000L
        val episodeAirTime = currentTime + 5.minutes.inWholeMilliseconds
        val bufferTime = 10.minutes

        dateTimeProvider.setCurrentTimeMillis(currentTime)
        datastoreRepository.setEpisodeNotificationsEnabled(true)

        val existingNotification = EpisodeNotification(
            id = 101,
            showId = 1,
            seasonId = 10,
            showName = "Test Show",
            episodeTitle = "Episode 1",
            seasonNumber = 1,
            episodeNumber = 1,
            imageUrl = null,
            scheduledTime = episodeAirTime - bufferTime.inWholeMilliseconds,
        )
        notificationManager.addPendingNotification(existingNotification)

        episodeRepository.setUpcomingEpisodes(
            listOf(
                UpcomingEpisode(
                    episodeId = 101,
                    seasonId = 10,
                    showId = 1,
                    episodeNumber = 1,
                    seasonNumber = 1,
                    title = "Episode 1",
                    overview = null,
                    runtime = null,
                    imageUrl = null,
                    firstAired = episodeAirTime,
                    showName = "Test Show",
                    showPoster = null,
                ),
            ),
        )

        interactor(ScheduleEpisodeNotificationsInteractor.Params(limit = 12.hours, bufferTime = bufferTime)).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        notificationManager.getScheduledNotifications().keys shouldContainExactlyInAnyOrder listOf(101L)
    }

    @Test
    fun `should schedule notification given episode airs in the future`() = runTest(testDispatcher) {
        val currentTime = 1_000_000L
        val episodeAirTime = currentTime + 1.hours.inWholeMilliseconds

        dateTimeProvider.setCurrentTimeMillis(currentTime)
        datastoreRepository.setEpisodeNotificationsEnabled(true)

        episodeRepository.setUpcomingEpisodes(
            listOf(
                UpcomingEpisode(
                    episodeId = 201,
                    seasonId = 20,
                    showId = 2,
                    episodeNumber = 3,
                    seasonNumber = 1,
                    title = "Future Episode",
                    overview = null,
                    runtime = null,
                    imageUrl = null,
                    firstAired = episodeAirTime,
                    showName = "Future Show",
                    showPoster = null,
                ),
            ),
        )

        interactor(ScheduleEpisodeNotificationsInteractor.Params()).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        val scheduled = notificationManager.getScheduledNotifications()
        scheduled.size shouldBe 1
        scheduled[201L]!!.scheduledTime shouldBe episodeAirTime - 10.minutes.inWholeMilliseconds
    }

    @Test
    fun `should cancel stale notifications given episode no longer upcoming`() = runTest(testDispatcher) {
        val currentTime = 1_000_000L
        dateTimeProvider.setCurrentTimeMillis(currentTime)
        datastoreRepository.setEpisodeNotificationsEnabled(true)

        val staleNotification = EpisodeNotification(
            id = 999,
            showId = 9,
            seasonId = 90,
            showName = "Cancelled Show",
            episodeTitle = "Old Episode",
            seasonNumber = 1,
            episodeNumber = 1,
            imageUrl = null,
            scheduledTime = currentTime + 1.hours.inWholeMilliseconds,
        )
        notificationManager.addPendingNotification(staleNotification)

        episodeRepository.setUpcomingEpisodes(emptyList())

        interactor(ScheduleEpisodeNotificationsInteractor.Params()).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        notificationManager.getScheduledNotifications().shouldBeEmpty()
    }

    @Test
    fun `should not cancel near-past notification given it was previously scheduled`() = runTest(testDispatcher) {
        val currentTime = 1_000_000L
        val episodeAirTime = currentTime + 5.minutes.inWholeMilliseconds
        val bufferTime = 10.minutes

        dateTimeProvider.setCurrentTimeMillis(currentTime)
        datastoreRepository.setEpisodeNotificationsEnabled(true)

        val previouslyScheduled = EpisodeNotification(
            id = 301,
            showId = 3,
            seasonId = 30,
            showName = "Almost Airing Show",
            episodeTitle = "Imminent Episode",
            seasonNumber = 2,
            episodeNumber = 5,
            imageUrl = null,
            scheduledTime = episodeAirTime - bufferTime.inWholeMilliseconds,
        )
        notificationManager.addPendingNotification(previouslyScheduled)

        val staleNotification = EpisodeNotification(
            id = 999,
            showId = 9,
            seasonId = 90,
            showName = "Old Show",
            episodeTitle = "Old Episode",
            seasonNumber = 1,
            episodeNumber = 1,
            imageUrl = null,
            scheduledTime = currentTime + 2.hours.inWholeMilliseconds,
        )
        notificationManager.addPendingNotification(staleNotification)

        episodeRepository.setUpcomingEpisodes(
            listOf(
                UpcomingEpisode(
                    episodeId = 301,
                    seasonId = 30,
                    showId = 3,
                    episodeNumber = 5,
                    seasonNumber = 2,
                    title = "Imminent Episode",
                    overview = null,
                    runtime = null,
                    imageUrl = null,
                    firstAired = episodeAirTime,
                    showName = "Almost Airing Show",
                    showPoster = null,
                ),
            ),
        )

        interactor(ScheduleEpisodeNotificationsInteractor.Params(limit = 12.hours, bufferTime = bufferTime)).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        val remaining = notificationManager.getScheduledNotifications()
        remaining.size shouldBe 1
        remaining shouldContainKey 301L
        remaining shouldNotContainKey 999L
    }

    @Test
    fun `should skip scheduling given notifications are disabled`() = runTest(testDispatcher) {
        datastoreRepository.setEpisodeNotificationsEnabled(false)

        episodeRepository.setUpcomingEpisodes(
            listOf(
                UpcomingEpisode(
                    episodeId = 401,
                    seasonId = 40,
                    showId = 4,
                    episodeNumber = 1,
                    seasonNumber = 1,
                    title = "Ignored Episode",
                    overview = null,
                    runtime = null,
                    imageUrl = null,
                    firstAired = 9_999_999L,
                    showName = "Ignored Show",
                    showPoster = null,
                ),
            ),
        )

        interactor(ScheduleEpisodeNotificationsInteractor.Params()).test {
            awaitItem() shouldBe InvokeStarted
            awaitItem() shouldBe InvokeSuccess
            awaitComplete()
        }

        notificationManager.getScheduledNotifications().shouldBeEmpty()
    }
}
