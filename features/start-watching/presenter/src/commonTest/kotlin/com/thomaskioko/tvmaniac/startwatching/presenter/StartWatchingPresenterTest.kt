package com.thomaskioko.tvmaniac.startwatching.presenter

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.startwatching.api.StartWatchingShow
import com.thomaskioko.tvmaniac.startwatching.presenter.model.StartWatchingItem
import com.thomaskioko.tvmaniac.startwatching.testing.FakeStartWatchingRepository
import com.thomaskioko.tvmaniac.traktauth.api.TraktAuthState
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
    private val factory = FakeStartWatchingPresenterBuilder()

    private lateinit var presenter: StartWatchingPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()
        presenter = factory.create(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit empty state given no shows`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe StartWatchingState()
        }
    }

    @Test
    fun `should emit items as they arrive given repository emits shows`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe StartWatchingState()

            factory.startWatchingRepository.setStartWatchingShows(startWatchingShows)

            val loaded = awaitItem()
            loaded.items shouldBe expectedItems
            loaded.isEmpty shouldBe false
            loaded.showLoading shouldBe false
        }
    }

    @Test
    fun `should show loading given syncing and no shows`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe StartWatchingState()

            factory.syncObserver.setSyncing(true)

            val syncing = awaitItem()
            syncing.isSyncing shouldBe true
            syncing.showLoading shouldBe true
        }
    }

    @Test
    fun `should sync watchlist without forcing refresh given auth state changes to logged in`() = runTest {
        factory.traktAuthRepository.setState(TraktAuthState.LOGGED_IN)
        testDispatcher.scheduler.advanceUntilIdle()

        factory.startWatchingRepository.syncInvocations() shouldBe listOf(
            FakeStartWatchingRepository.SyncInvocation(forceRefresh = false),
        )
    }

    @Test
    fun `should force refresh watchlist given RefreshStartWatching action is dispatched`() = runTest {
        presenter.dispatch(RefreshStartWatching())
        testDispatcher.scheduler.advanceUntilIdle()

        factory.startWatchingRepository.syncInvocations() shouldBe listOf(
            FakeStartWatchingRepository.SyncInvocation(forceRefresh = true),
        )
    }

    @Test
    fun `should not show loading given syncing but shows already present`() = runTest {
        presenter.state.test {
            awaitItem() shouldBe StartWatchingState()

            factory.startWatchingRepository.setStartWatchingShows(startWatchingShows)
            awaitItem().items shouldBe expectedItems

            factory.syncObserver.setSyncing(true)

            val syncing = awaitItem()
            syncing.isSyncing shouldBe true
            syncing.showLoading shouldBe false
            syncing.items shouldBe expectedItems
        }
    }
}
