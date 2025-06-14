package com.thomaskioko.tvmaniac.navigation

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.testing.FakeDatastoreRepository
import com.thomaskioko.tvmaniac.discover.presenter.di.FakeDiscoverPresenterFactory
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.Home
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.MoreShows
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.ShowDetails
import com.thomaskioko.tvmaniac.presenter.home.DefaultHomePresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.moreshows.FakeMoreShowsPresenterFactory
import com.thomaskioko.tvmaniac.presenter.showdetails.FakeShowDetailsPresenterFactory
import com.thomaskioko.tvmaniac.presenter.trailers.FakeTrailersPresenterFactory
import com.thomaskioko.tvmaniac.presenter.trailers.di.TrailersPresenterFactory
import com.thomaskioko.tvmaniac.search.presenter.FakeSearchPresenterFactory
import com.thomaskioko.tvmaniac.seasondetails.presenter.FakeSeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.seasondetails.presenter.di.SeasonDetailsPresenterFactory
import com.thomaskioko.tvmaniac.settings.presenter.FakeSettingsPresenterFactory
import com.thomaskioko.tvmaniac.traktauth.testing.FakeTraktAuthManager
import com.thomaskioko.tvmaniac.watchlist.presenter.FakeWatchlistPresenterFactory
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

class DefaultRootComponentTest {
    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()
    private val traktAuthManager = FakeTraktAuthManager()
    private val datastoreRepository = FakeDatastoreRepository()

    private lateinit var presenter: DefaultRootPresenter
    private lateinit var navigator: FakeRootNavigator

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()

        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        navigator = FakeRootNavigator()
        presenter = DefaultRootPresenter(
            componentContext = componentContext,
            navigator = navigator,
            moreShowsPresenterFactory = FakeMoreShowsPresenterFactory(),
            showDetailsPresenterFactory = FakeShowDetailsPresenterFactory(),
            seasonDetailsPresenterFactory = buildSeasonDetailsPresenterFactory(),
            trailersPresenterFactory = buildTrailersPresenterFactory(),
            homePresenterFactory = buildHomePresenterFactory(),
            datastoreRepository = datastoreRepository,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Home`() = runTest {
        presenter.childStack.test { awaitItem().active.instance.shouldBeInstanceOf<Home>() }
    }

    @Test
    fun `should return Home as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.bringToFront(RootDestinationConfig.ShowDetails(1))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<ShowDetails>()

            navigator.pop()

            awaitItem().active.instance.shouldBeInstanceOf<Home>()
        }
    }

    @Test
    fun `should return ShowDetails as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.bringToFront(RootDestinationConfig.ShowDetails(1))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<ShowDetails>()
        }
    }

    @Test
    fun `should return MoreShows as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.bringToFront(RootDestinationConfig.MoreShows(1))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<MoreShows>()
        }
    }

    @Test
    fun `should return initial theme state`() = runTest {
        presenter.themeState.value shouldBe ThemeState()
    }

    @Test
    fun `should update theme to Dark when DarkTheme is set`() = runTest {
        presenter.themeState.test {
            awaitItem() shouldBe ThemeState()

            datastoreRepository.setTheme(AppTheme.DARK_THEME)

            awaitItem() shouldBe
                ThemeState(
                    isFetching = false,
                    appTheme = AppTheme.DARK_THEME,
                )
        }
    }

    private fun buildHomePresenterFactory(): HomePresenter.Factory =
        DefaultHomePresenter.Factory(
            traktAuthManager = traktAuthManager,
            searchPresenterFactory = FakeSearchPresenterFactory(),
            settingsPresenterFactory = FakeSettingsPresenterFactory(),
            discoverPresenterFactory = FakeDiscoverPresenterFactory(),
            watchlistPresenterFactory = FakeWatchlistPresenterFactory(),
        )

    private fun buildSeasonDetailsPresenterFactory(): SeasonDetailsPresenterFactory = FakeSeasonDetailsPresenterFactory()

    private fun buildTrailersPresenterFactory(): TrailersPresenterFactory = FakeTrailersPresenterFactory()
}
