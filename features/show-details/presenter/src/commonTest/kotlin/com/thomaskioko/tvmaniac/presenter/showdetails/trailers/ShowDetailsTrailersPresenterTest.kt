package com.thomaskioko.tvmaniac.presenter.showdetails.trailers

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.thomaskioko.tvmaniac.accountmanager.testing.FakeAccountManager
import com.thomaskioko.tvmaniac.core.base.model.AppCoroutineDispatchers
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.core.view.ErrorToStringMapper
import com.thomaskioko.tvmaniac.domain.showdetails.FetchTrailersInteractor
import com.thomaskioko.tvmaniac.domain.showdetails.ObserveTrailersInteractor
import com.thomaskioko.tvmaniac.navigation.testing.FakeNavigator
import com.thomaskioko.tvmaniac.presenter.showdetails.model.TrailerModel
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
import com.thomaskioko.tvmaniac.trailers.testing.FakeTrailerRepository
import com.thomaskioko.tvmaniac.trailers.testing.trailers
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class ShowDetailsTrailersPresenterTest {

    private val testDispatcher = StandardTestDispatcher()
    private val dispatchers = AppCoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        computation = testDispatcher,
        databaseWrite = testDispatcher,
        databaseRead = testDispatcher,
    )

    private val trailerRepository = FakeTrailerRepository()
    private val accountManager = FakeAccountManager()
    private val navigator = FakeNavigator()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should map trailers to state given trailers are available`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            trailerRepository.setYoutubePlayerInstalled(true)
            trailerRepository.setTrailerResult(trailers)

            var state = awaitItem()
            while (state.trailersList.isEmpty()) {
                state = awaitItem()
            }

            state.trailersList shouldBe listOf(
                TrailerModel(
                    showId = 84958L,
                    key = "Fd43V",
                    name = "Some title",
                    youtubeThumbnailUrl = "https://i.ytimg.com/vi/Fd43V/hqdefault.jpg",
                ),
            )
            state.hasWebViewInstalled shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should expose webview availability given youtube player is not installed`() = runTest {
        val presenter = buildPresenter()

        presenter.state.test {
            trailerRepository.setYoutubePlayerInstalled(false)
            trailerRepository.setTrailerResult(trailers)

            var state = awaitItem()
            while (state.trailersList.isEmpty()) {
                state = awaitItem()
            }

            state.hasWebViewInstalled shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should navigate to trailer given watch trailer clicked`() = runTest {
        val presenter = buildPresenter()
        testDispatcher.scheduler.advanceUntilIdle()

        presenter.dispatch(ShowDetailsWatchTrailerClicked(id = SHOW_ID))

        navigator.lastNavigatedRoute.shouldBeInstanceOf<TrailersRoute>()
    }

    private fun buildPresenter(forceRefresh: Boolean = false): ShowDetailsTrailersPresenter =
        ShowDetailsTrailersPresenter(
            componentContext = DefaultComponentContext(lifecycle = LifecycleRegistry()),
            showId = SHOW_ID,
            forceRefresh = forceRefresh,
            observeTrailersInteractor = ObserveTrailersInteractor(
                trailerRepository = trailerRepository,
                dispatchers = dispatchers,
            ),
            fetchTrailersInteractor = FetchTrailersInteractor(
                trailerRepository = trailerRepository,
                dispatchers = dispatchers,
            ),
            navigator = navigator,
            accountManager = accountManager,
            errorToStringMapper = ErrorToStringMapper { it.message ?: "Test error" },
            logger = FakeLogger(),
        )

    private companion object {
        private const val SHOW_ID = 84958L
    }
}
