package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.subscription.testing.FakeSubscriptionManager
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WidgetRefreshWorkerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeUpNextRepository()
    private val interactor = ObserveWidgetShowsInteractor(repository)
    private val themeInteractor = ObserveWidgetThemeInteractor(
        datastoreRepository = FakeDatastoreRepository(),
        subscriptionManager = FakeSubscriptionManager(),
    )

    @Test
    fun `should publish the shows given a widget is installed`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = FakeWidgetPublisher().apply { setInstalled(true) }

        buildWorker(publisher).doWork() shouldBe WorkerResult.Success

        publisher.getPublishedShows()?.single()?.showName shouldBe "Breaking Bad"
    }

    @Test
    fun `should publish nothing given no widget is installed`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = FakeWidgetPublisher().apply { setInstalled(false) }

        buildWorker(publisher).doWork() shouldBe WorkerResult.Success

        publisher.getPublishedShows() shouldBe null
    }

    @Test
    fun `should ask to retry given publishing fails`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val cause = IllegalStateException("no container")
        val publisher = FakeWidgetPublisher().apply {
            setInstalled(true)
            setFailure(cause)
        }

        val result = buildWorker(publisher).doWork()

        result.shouldBeInstanceOf<WorkerResult.Retry>()
        result.message shouldBe "no container"
        result.cause shouldBe cause
    }

    @Test
    fun `should not log the failure itself given publishing fails`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = FakeWidgetPublisher().apply {
            setInstalled(true)
            setFailure(IllegalStateException("no container"))
        }
        val logger = FakeLogger()

        buildWorker(publisher, logger).doWork()

        logger.recordedErrors.size shouldBe 0
        logger.breadcrumbs.size shouldBe 0
    }

    @Test
    fun `should publish an empty list given an empty watchlist`() = runTest(testDispatcher) {
        val publisher = FakeWidgetPublisher().apply { setInstalled(true) }

        buildWorker(publisher).doWork() shouldBe WorkerResult.Success

        publisher.getPublishedShows() shouldBe emptyList()
    }

    private fun buildWorker(publisher: WidgetPublisher, logger: FakeLogger = FakeLogger()) = WidgetRefreshWorker(
        observeWidgetShowsInteractor = interactor,
        observeWidgetThemeInteractor = themeInteractor,
        widgetPublishers = setOf(publisher),
        logger = logger,
    )

    private fun episode() = NextEpisodeWithShow(
        showId = 1396,
        showName = "Breaking Bad",
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
