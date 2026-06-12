package com.thomaskioko.tvmaniac.domain.continuewatching

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
internal class ObserveWatchlistPreviewInteractorTest {
    private val testDispatcher = StandardTestDispatcher()
    private val upNextRepository = FakeUpNextRepository()

    private lateinit var interactor: ObserveWatchlistPreviewInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveWatchlistPreviewInteractor(upNextRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit empty list when watchlist is empty`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(emptyList())

        interactor(ObserveWatchlistPreviewInteractor.Param())

        interactor.flow.test {
            awaitItem().shouldBeEmpty()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should exclude completed shows from the preview`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(
            listOf(
                createNextEpisode(showId = 1, showName = "In Progress", watchedCount = 5, totalCount = 10),
                createNextEpisode(showId = 2, showName = "Completed", watchedCount = 10, totalCount = 10),
            ),
        )

        interactor(ObserveWatchlistPreviewInteractor.Param())

        interactor.flow.test {
            val result = awaitItem()
            result.size shouldBe 1
            result[0].title shouldBe "In Progress"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should cap the preview at the requested limit`() = runTest {
        upNextRepository.setNextEpisodesForWatchlist(
            (1L..5L).map { createNextEpisode(showId = it, showName = "Show $it") },
        )

        interactor(ObserveWatchlistPreviewInteractor.Param(limit = 2))

        interactor.flow.test {
            awaitItem().size shouldBe 2
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun createNextEpisode(
        showId: Long,
        showName: String,
        watchedCount: Long = 0,
        totalCount: Long = 10,
    ) = NextEpisodeWithShow(
        showId = showId,
        showName = showName,
        showPoster = "/poster.jpg",
        showStatus = "Ended",
        showYear = "2024",
        episodeId = 1L,
        episodeName = "Episode 1",
        seasonId = 1L,
        seasonNumber = 1L,
        episodeNumber = 1L,
        runtime = 45L,
        stillPath = "/still.jpg",
        overview = "Overview",
        firstAired = null,
        lastWatchedAt = null,
        seasonCount = 2,
        episodeCount = 12,
        watchedCount = watchedCount,
        totalCount = totalCount,
    )
}
