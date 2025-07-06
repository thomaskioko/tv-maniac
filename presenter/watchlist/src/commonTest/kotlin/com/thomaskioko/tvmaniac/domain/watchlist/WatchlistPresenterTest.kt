package com.thomaskioko.tvmaniac.domain.watchlist

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.watchlist.presenter.ChangeListStyleClicked
import com.thomaskioko.tvmaniac.watchlist.presenter.FakeWatchlistPresenterFactory
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistQueryChanged
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistState
import io.kotest.matchers.shouldBe
import kotlinx.collections.immutable.persistentListOf
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
        presenter = factory.invoke(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigateToShowDetails = {},
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should emit initial state on init`() = runTest {
        presenter.state.value shouldBe WatchlistState()
    }

    @Test
    fun `should emit WatchlistState with content on success`() = runTest {
        presenter.state.test {
            val initialState = awaitItem()
            initialState.query shouldBe ""
            initialState.isSearchActive shouldBe false
            initialState.isGridMode shouldBe true
            initialState.isLoading shouldBe false
            initialState.items shouldBe persistentListOf()

            factory.repository.setObserveResult(cachedResult)

            val firstUpdate = awaitItem()
            firstUpdate.items shouldBe expectedUiResult(cachedResult)

            factory.repository.setObserveResult(updatedData)

            val secondUpdate = awaitItem()
            secondUpdate.items shouldBe expectedUiResult()
        }
    }

    @Test
    fun `should toggle list style when ChangeListStyleClicked is dispatched`() = runTest {
        factory.repository.setObserveResult(cachedResult)

        presenter.state.test {
            val initialState = awaitItem()
            initialState.isGridMode shouldBe true

            // Dispatch action to toggle list style
            presenter.dispatch(ChangeListStyleClicked)

            val updatedState = awaitItem()
            updatedState.isGridMode shouldBe false
            updatedState.query shouldBe initialState.query
            updatedState.items shouldBe expectedUiResult(cachedResult)

            // Toggle back to grid mode
            presenter.dispatch(ChangeListStyleClicked)

            val finalState = awaitItem()
            finalState.isGridMode shouldBe true
        }
    }

    @Test
    fun `should update query and search state when WatchlistQueryChanged is dispatched`() = runTest {
        factory.repository.setObserveResult(cachedResult)
        factory.repository.setSearchResult(emptyList())

        presenter.state.test {
            val initialState = awaitItem()
            initialState.query shouldBe ""
            initialState.isSearchActive shouldBe false

            // Dispatch action to change query
            presenter.dispatch(WatchlistQueryChanged("test query"))

            val updatedState = awaitItem()
            updatedState.query shouldBe "test query"
            updatedState.isSearchActive shouldBe true
        }
    }
}
