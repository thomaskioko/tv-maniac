package com.thomaskioko.tvmaniac.domain.widget

import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.tasks.api.WorkerResult
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WidgetRefreshWorkerTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeUpNextRepository()
    private val interactor = ObserveWidgetShowsInteractor(repository)

    @Test
    fun `should publish the shows given a widget is installed`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = RecordingWidgetPublisher(installed = true)

        buildWorker(publisher).doWork() shouldBe WorkerResult.Success

        publisher.published?.single()?.showName shouldBe "Breaking Bad"
    }

    @Test
    fun `should publish nothing given no widget is installed`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = RecordingWidgetPublisher(installed = false)

        buildWorker(publisher).doWork() shouldBe WorkerResult.Success

        publisher.published shouldBe null
    }

    @Test
    fun `should ask to retry given publishing fails`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode()))
        val publisher = RecordingWidgetPublisher(installed = true, failWith = IllegalStateException("no container"))

        buildWorker(publisher).doWork() shouldBe WorkerResult.Retry("no container")
    }

    @Test
    fun `should publish an empty list given an empty watchlist`() = runTest(testDispatcher) {
        val publisher = RecordingWidgetPublisher(installed = true)

        buildWorker(publisher).doWork() shouldBe WorkerResult.Success

        publisher.published shouldBe emptyList()
    }

    private fun buildWorker(publisher: WidgetPublisher) = WidgetRefreshWorker(
        observeWidgetShowsInteractor = interactor,
        widgetPublishers = setOf(publisher),
        logger = FakeLogger(),
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

private class RecordingWidgetPublisher(
    private val installed: Boolean,
    private val failWith: Exception? = null,
) : WidgetPublisher {

    var published: List<WidgetShow>? = null
        private set

    override suspend fun hasInstalledWidgets(): Boolean = installed

    override suspend fun publish(shows: List<WidgetShow>) {
        failWith?.let { throw it }
        published = shows
    }
}
