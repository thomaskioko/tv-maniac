package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.core.networkutil.model.Either
import com.thomaskioko.tvmaniac.watchlist.presenter.FakeWatchlistPresenterFactory
import com.thomaskioko.tvmaniac.watchlist.presenter.LoadingShows
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistContent
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class WatchlistPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val factory = FakeWatchlistPresenterFactory()

    private lateinit var presenter: WatchlistPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)

        lifecycle.resume()
        presenter = factory.create(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigateToShowDetails = {},
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit LoadingShows on init`() = runTest { presenter.state.value shouldBe LoadingShows }

    @Test
    fun `should emit LibraryContent on success`() = runTest {
        factory.repository.setObserveResult(Either.Right(cachedResult))

        presenter.state.test {
            awaitItem() shouldBe LoadingShows
            awaitItem() shouldBe WatchlistContent(query = "", list = uiResult)

            factory.repository.setObserveResult(Either.Right(updatedData))

            awaitItem() shouldBe WatchlistContent(query = "", list = expectedUiResult())
        }
    }
}
