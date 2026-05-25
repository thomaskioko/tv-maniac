package com.thomaskioko.tvmaniac.startwatching.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.domain.startwatching.ObserveStartWatchingInteractor
import com.thomaskioko.tvmaniac.navigation.testing.NoOpNavigator
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import com.thomaskioko.tvmaniac.watchlistprefs.testing.FakeWatchlistPrefsRepository
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

private val startWatchingShows = listOf(
    StartWatchingShow(traktId = 1, tmdbId = 1, title = "Breaking Bad", posterPath = "/1.jpg", year = "2008", inLibrary = true),
    StartWatchingShow(traktId = 2, tmdbId = 2, title = "Better Call Saul", posterPath = "/2.jpg", year = "2015", inLibrary = true),
)

private val expectedItems = listOf(
    StartWatchingItem(traktId = 1, title = "Breaking Bad", posterImageUrl = "/1.jpg", year = "2008"),
    StartWatchingItem(traktId = 2, title = "Better Call Saul", posterImageUrl = "/2.jpg", year = "2015"),
)

class StartWatchingPresenterTest {

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val repository = FakeStartWatchingRepository()
    private val prefsRepository = FakeWatchlistPrefsRepository()

    private lateinit var presenter: StartWatchingPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()
        presenter = StartWatchingPresenter(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigator = NoOpNavigator(),
            observeStartWatchingInteractor = ObserveStartWatchingInteractor(repository = repository),
            repository = prefsRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit loading then empty given no shows`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe StartWatchingState(isLoading = true)
            awaitItem() shouldBe StartWatchingState(isLoading = false)
        }
    }

    @Test
    fun `should emit items given interactor emits shows`() = runTest {
        repository.setStartWatchingShows(startWatchingShows)

        presenter.state.test {
            awaitItem() shouldBe StartWatchingState(isLoading = true)

            val loaded = awaitItem()
            loaded.isLoading shouldBe false
            loaded.items shouldBe expectedItems
        }
    }
}
