package com.thomaskioko.tvmaniac.domain.widget

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.domain.widget.model.WidgetShow
import com.thomaskioko.tvmaniac.upnext.api.model.NextEpisodeWithShow
import com.thomaskioko.tvmaniac.upnext.testing.FakeUpNextRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveWidgetShowsInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeUpNextRepository()
    private val interactor = ObserveWidgetShowsInteractor(repository)

    @Test
    fun `should return an empty list given an empty watchlist`() = runTest(testDispatcher) {
        interactor.flow.test {
            interactor(Unit)

            awaitItem() shouldBe emptyList()
        }
    }

    @Test
    fun `should map a show given the next episode is known`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode(showId = 1396)))

        interactor.flow.test {
            interactor(Unit)

            awaitItem() shouldBe listOf(
                WidgetShow(
                    tmdbId = 1396,
                    showName = "Breaking Bad",
                    episodeName = "Pilot",
                    seasonNumber = 1,
                    episodeNumber = 1,
                    posterUrl = "/poster.jpg",
                ),
            )
        }
    }

    @Test
    fun `should return at most six entries given more are watched`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist((1L..10L).map { episode(showId = it) })

        interactor.flow.test {
            interactor(Unit)

            awaitItem().size shouldBe ObserveWidgetShowsInteractor.MAX_ENTRIES
        }
    }

    @Test
    fun `should skip a show given every tracked episode is watched`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(
            listOf(episode(showId = 1396, watchedCount = 62, totalCount = 62)),
        )

        interactor.flow.test {
            interactor(Unit)

            awaitItem() shouldBe emptyList()
        }
    }

    @Test
    fun `should skip a show given no season number is stored`() = runTest(testDispatcher) {
        repository.setNextEpisodesForWatchlist(listOf(episode(showId = 1396).copy(seasonNumber = null)))

        interactor.flow.test {
            interactor(Unit)

            awaitItem() shouldBe emptyList()
        }
    }

    private fun episode(
        showId: Long,
        watchedCount: Long = 0,
        totalCount: Long = 62,
    ) = NextEpisodeWithShow(
        showId = showId,
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
        watchedCount = watchedCount,
        totalCount = totalCount,
    )
}
