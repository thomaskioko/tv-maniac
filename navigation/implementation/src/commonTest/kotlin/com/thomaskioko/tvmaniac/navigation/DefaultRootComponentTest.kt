package com.thomaskioko.tvmaniac.navigation

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.Home
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.MoreShows
import com.thomaskioko.tvmaniac.navigation.RootPresenter.Child.ShowDetails
import com.thomaskioko.tvmaniac.seasondetails.presenter.model.SeasonDetailsUiParam
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

abstract class DefaultRootComponentTest {
    abstract val rootPresenterFactory: DefaultRootPresenter.Factory
    abstract val datastoreRepository: DatastoreRepository

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var presenter: RootPresenter
    private lateinit var navigator: FakeRootNavigator

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()

        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        navigator = FakeRootNavigator()
        presenter = rootPresenterFactory.create(componentContext, navigator)
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
    fun `should return SeasonDetails as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            val param = SeasonDetailsUiParam(
                showId = 1,
                seasonId = 2,
                seasonNumber = 3,
            )
            navigator.bringToFront(RootDestinationConfig.SeasonDetails(param))

            val seasonDetailsScreen = awaitItem().active.instance

            seasonDetailsScreen.shouldBeInstanceOf<RootPresenter.Child.SeasonDetails>()
        }
    }

    @Test
    fun `should return Trailers as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.bringToFront(RootDestinationConfig.Trailers(1))

            val trailersScreen = awaitItem().active.instance

            trailersScreen.shouldBeInstanceOf<RootPresenter.Child.Trailers>()
        }
    }

    @Test
    fun `should return GenreShows as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.bringToFront(RootDestinationConfig.GenreShows(1))

            val genreShowsScreen = awaitItem().active.instance

            genreShowsScreen.shouldBeInstanceOf<RootPresenter.Child.GenreShows>()
        }
    }

    @Test
    fun `should navigate to ShowDetails using pushNew`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.pushNew(RootDestinationConfig.ShowDetails(1))

            val showDetailsScreen = awaitItem().active.instance

            showDetailsScreen.shouldBeInstanceOf<ShowDetails>()
        }
    }

    @Test
    fun `should navigate to ShowDetails using pushToFront`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.pushToFront(RootDestinationConfig.ShowDetails(1))

            val showDetailsScreen = awaitItem().active.instance

            showDetailsScreen.shouldBeInstanceOf<ShowDetails>()
        }
    }

    @Test
    fun `should navigate back to previous screen using popTo`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<Home>()

            navigator.pushNew(RootDestinationConfig.ShowDetails(1))
            awaitItem().active.instance.shouldBeInstanceOf<ShowDetails>()

            navigator.pushNew(RootDestinationConfig.MoreShows(1))
            awaitItem().active.instance.shouldBeInstanceOf<MoreShows>()

            navigator.popTo(0) // Pop back to Home

            awaitItem().active.instance.shouldBeInstanceOf<Home>()
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

            datastoreRepository.saveTheme(AppTheme.DARK_THEME)

            awaitItem() shouldBe
                ThemeState(
                    isFetching = false,
                    appTheme = AppTheme.DARK_THEME,
                )
        }
    }

    @Test
    fun `should update theme to Light when LightTheme is set`() = runTest {
        presenter.themeState.test {
            awaitItem() shouldBe ThemeState()

            datastoreRepository.saveTheme(AppTheme.LIGHT_THEME)

            awaitItem() shouldBe
                ThemeState(
                    isFetching = false,
                    appTheme = AppTheme.LIGHT_THEME,
                )
        }
    }
}
