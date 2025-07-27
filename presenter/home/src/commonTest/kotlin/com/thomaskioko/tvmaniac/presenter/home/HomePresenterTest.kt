package com.thomaskioko.tvmaniac.presenter.home

import app.cash.turbine.test
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.presenter.home.fakes.FakeDiscoverShowsPresenterFactory
import com.thomaskioko.tvmaniac.presenter.home.fakes.FakeSearchPresenterFactory
import com.thomaskioko.tvmaniac.presenter.home.fakes.FakeSettingsPresenterFactory
import com.thomaskioko.tvmaniac.presenter.home.fakes.FakeWatchlistPresenterFactory
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.BeforeTest
import kotlin.test.Test

class HomePresenterTest {
    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val traktAuthManager = FakeTraktAuthManager()

    private lateinit var presenter: DefaultHomePresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()

        presenter = buildHomePresenterFactory(
            DefaultComponentContext(lifecycle = lifecycle),
        )
    }

    @Test
    fun `initial state should be Discover`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
        }
    }

    @Test
    fun `should return Search as active instance when onSearchClicked`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
            presenter.onSearchClicked()

            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Search>()
        }
    }

    @Test
    fun `should return Library as active instance when onSettingsClicked`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
            presenter.onLibraryClicked()

            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Watchlist>()
        }
    }

    @Test
    fun `should return Settings as active instance when onSettingsClicked`() = runTest {
        presenter.homeChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Discover>()
            presenter.onSettingsClicked()

            awaitItem().active.instance.shouldBeInstanceOf<HomePresenter.Child.Settings>()
        }
    }

    private fun buildHomePresenterFactory(
        componentContext: ComponentContext,
    ): DefaultHomePresenter =
        DefaultHomePresenter(
            componentContext = componentContext,
            traktAuthManager = traktAuthManager,
            searchPresenterFactory = FakeSearchPresenterFactory(),
            settingsPresenterFactory = FakeSettingsPresenterFactory(),
            discoverPresenterFactory = FakeDiscoverShowsPresenterFactory(),
            watchlistPresenterFactory = FakeWatchlistPresenterFactory(),
            onShowClicked = {},
            onMoreShowClicked = {},
            onShowGenreClicked = {},
        )
}
