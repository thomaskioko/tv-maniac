package com.thomaskioko.tvmaniac.domain.continuewatching

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.upnext.api.model.CompletedShow
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
internal class ObserveCompletedShowsInteractorTest {
    private val testDispatcher = StandardTestDispatcher()
    private val upNextRepository = FakeUpNextRepository()

    private lateinit var interactor: ObserveCompletedShowsInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveCompletedShowsInteractor(upNextRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit empty list when no shows are completed`() = runTest {
        upNextRepository.setCompletedShows(emptyList())

        interactor(ObserveCompletedShowsInteractor.Param())

        interactor.flow.test {
            awaitItem().shouldBeEmpty()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit completed shows from the repository`() = runTest {
        upNextRepository.setCompletedShows(
            listOf(
                createCompletedShow(showId = 1, showName = "Breaking Bad"),
                createCompletedShow(showId = 2, showName = "The Wire"),
            ),
        )

        interactor(ObserveCompletedShowsInteractor.Param())

        interactor.flow.test {
            val result = awaitItem()
            result.size shouldBe 2
            result[0].showName shouldBe "Breaking Bad"
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should cap the result at the requested limit`() = runTest {
        upNextRepository.setCompletedShows(
            (1L..5L).map { createCompletedShow(showId = it, showName = "Show $it") },
        )

        interactor(ObserveCompletedShowsInteractor.Param(limit = 2))

        interactor.flow.test {
            awaitItem().size shouldBe 2
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun createCompletedShow(
        showId: Long,
        showName: String,
    ) = CompletedShow(
        showId = showId,
        showTmdbId = showId,
        showName = showName,
        showPoster = "/poster.jpg",
        lastWatchedAt = 0L,
        watchedCount = 10,
        totalCount = 10,
    )
}
