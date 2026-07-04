package com.thomaskioko.tvmaniac.domain.showdetails

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.domain.showdetails.model.Trailer
import com.thomaskioko.tvmaniac.domain.showdetails.model.TrailersResult
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ObserveTrailersInteractorTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
        main = testDispatcher,
    )
    private val trailerRepository = FakeTrailerRepository()
    private lateinit var interactor: ObserveTrailersInteractor

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        interactor = ObserveTrailersInteractor(
            trailerRepository = trailerRepository,
            dispatchers = dispatchers,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit trailers with player status given trailer data`() = runTest {
        trailerRepository.setTrailerResult(trailers)
        trailerRepository.setYoutubePlayerInstalled(true)
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe TrailersResult(
                trailers = listOf(
                    Trailer(
                        showId = 84958L,
                        key = "Fd43V",
                        name = "Some title",
                        youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
                    ),
                ),
                hasWebViewInstalled = true,
            )
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `should emit empty trailer list given no trailers`() = runTest {
        trailerRepository.setTrailerResult(emptyList())
        trailerRepository.setYoutubePlayerInstalled(false)
        interactor(84958L)

        interactor.flow.test {
            awaitItem() shouldBe TrailersResult(
                trailers = emptyList(),
                hasWebViewInstalled = false,
            )
            cancelAndConsumeRemainingEvents()
        }
    }
}
