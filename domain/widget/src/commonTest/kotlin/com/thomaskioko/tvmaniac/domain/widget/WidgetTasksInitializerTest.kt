package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.testing.FakeBackgroundTaskScheduler
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WidgetTasksInitializerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val scheduler = FakeBackgroundTaskScheduler()
    private val repository = FakeUpNextRepository()
    private val interactor = ObserveWidgetShowsInteractor(repository)

    @Test
    fun `should schedule the refresh worker given a widget is added`() = runTest(testDispatcher) {
        val publisher = FakeWidgetPublisher().apply { setInstalled(true) }

        buildInitializer(publisher).init()
        runCurrent()

        scheduler.getScheduledRequests().single().id shouldBe WidgetRefreshWorker.WORKER_NAME
    }

    @Test
    fun `should cancel the refresh worker given no widget is added`() = runTest(testDispatcher) {
        val publisher = FakeWidgetPublisher().apply { setInstalled(false) }

        buildInitializer(publisher).init()
        runCurrent()

        scheduler.getScheduledRequests() shouldBe emptyList()
        scheduler.getCancelledIds().single() shouldBe WidgetRefreshWorker.WORKER_NAME
    }

    @Test
    fun `should touch no schedule given no publisher exists`() = runTest(testDispatcher) {
        buildInitializer().init()
        runCurrent()

        scheduler.getScheduledRequests() shouldBe emptyList()
        scheduler.getCancelledIds() shouldBe emptyList()
    }

    @Test
    fun `should publish the shows given a widget is added`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = FakeWidgetPublisher().apply { setInstalled(true) }

        buildInitializer(publisher).init()
        runCurrent()

        publisher.getPublishedShows()?.single()?.showName shouldBe "Breaking Bad"
    }

    @Test
    fun `should publish nothing given no widget is added`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = FakeWidgetPublisher().apply { setInstalled(false) }

        buildInitializer(publisher).init()
        runCurrent()

        publisher.getPublishedShows() shouldBe null
    }

    @Test
    fun `should publish again given the watchlist changed`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = FakeWidgetPublisher().apply { setInstalled(true) }

        buildInitializer(publisher).init()
        runCurrent()

        repository.setNextEpisodesForWatchlist(listOf(episode(showId = 60059, showName = "Better Call Saul")))
        runCurrent()

        publisher.getPublishedShows()?.single()?.showName shouldBe "Better Call Saul"
    }

    @Test
    fun `should keep collecting given publishing fails`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = FakeWidgetPublisher().apply {
            setInstalled(true)
            setFailure(IllegalStateException("no container"))
        }

        buildInitializer(publisher).init()
        runCurrent()

        scheduler.getScheduledRequests().single().id shouldBe WidgetRefreshWorker.WORKER_NAME
    }

    private val collectScope = CoroutineScope(testDispatcher + Job())

    @AfterTest
    fun tearDown() {
        collectScope.cancel()
    }

    private fun buildInitializer(vararg publishers: WidgetPublisher) = WidgetTasksInitializer(
        scheduler = scheduler,
        observeWidgetShowsInteractor = interactor,
        widgetPublishers = publishers.toSet(),
        logger = FakeLogger(),
        coroutineScope = collectScope,
    )

    private fun episode(showId: Long = 1396, showName: String = "Breaking Bad") = NextEpisodeWithShow(
        showId = showId,
        showName = showName,
        showPoster = "/poster.jpg",
        showStatus = "Ended",
        showYear = "2008",
        episodeId = 1,
        episodeName = "Pilot",
        seasonId = 1,
        seasonNumber = 1,
        episodeNumber = 1,
        runtime = 45,
        stillPath = "/still.jpg",
        overview = "",
        watchedCount = 0,
        totalCount = 62,
    )
}
