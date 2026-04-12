package com.thomaskioko.tvmaniac.presenter.root

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.root.model.DeepLinkDestination
import com.thomaskioko.root.model.NotificationPermissionState
import com.thomaskioko.root.model.ThemeState
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.debug.presenter.DebugDestination
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsDestination
import com.thomaskioko.tvmaniac.navigation.FakeRootNavigator
import com.thomaskioko.tvmaniac.navigation.GenreShowsDestination
import com.thomaskioko.tvmaniac.navigation.model.RootDestinationConfig
import com.thomaskioko.tvmaniac.presenter.home.HomeDestination
import com.thomaskioko.tvmaniac.presenter.root.RootPresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsDestination
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsParam
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersDestination
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsDestination
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsUiParam
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

abstract class DefaultRootPresenterTest {
    abstract val rootPresenterFactory: RootPresenter.Factory
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
        presenter = rootPresenterFactory(componentContext, navigator)
    }

    @AfterTest
    fun tearDown() {
        lifecycle.destroy()
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should be Home`() = runTest {
        presenter.childStack.test { awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>() }
    }

    @Test
    fun `should return Home as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            navigator.bringToFront(RootDestinationConfig.ShowDetails(ShowDetailsParam(1)))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<ShowDetailsDestination>()

            navigator.pop()

            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()
        }
    }

    @Test
    fun `should return ShowDetails as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            navigator.bringToFront(RootDestinationConfig.ShowDetails(ShowDetailsParam(1)))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<ShowDetailsDestination>()
        }
    }

    @Test
    fun `should return MoreShows as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            navigator.bringToFront(RootDestinationConfig.MoreShows(1))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<MoreShowsDestination>()
        }
    }

    @Test
    fun `should return SeasonDetails as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            val param = SeasonDetailsUiParam(
                showTraktId = 1,
                seasonId = 2,
                seasonNumber = 3,
            )
            navigator.bringToFront(RootDestinationConfig.SeasonDetails(param))

            val seasonDetailsScreen = awaitItem().active.instance

            seasonDetailsScreen.shouldBeInstanceOf<SeasonDetailsDestination>()
        }
    }

    @Test
    fun `should return Trailers as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            navigator.bringToFront(RootDestinationConfig.Trailers(1))

            val trailersScreen = awaitItem().active.instance

            trailersScreen.shouldBeInstanceOf<TrailersDestination>()
        }
    }

    @Test
    fun `should return GenreShows as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            navigator.bringToFront(RootDestinationConfig.GenreShows(1))

            val genreShowsScreen = awaitItem().active.instance

            genreShowsScreen.shouldBeInstanceOf<GenreShowsDestination>()
        }
    }

    @Test
    fun `should navigate to ShowDetails using pushNew`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            navigator.pushNew(RootDestinationConfig.ShowDetails(ShowDetailsParam(1)))

            val showDetailsScreen = awaitItem().active.instance

            showDetailsScreen.shouldBeInstanceOf<ShowDetailsDestination>()
        }
    }

    @Test
    fun `should navigate to ShowDetails using pushToFront`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            navigator.pushToFront(RootDestinationConfig.ShowDetails(ShowDetailsParam(1)))

            val showDetailsScreen = awaitItem().active.instance

            showDetailsScreen.shouldBeInstanceOf<ShowDetailsDestination>()
        }
    }

    @Test
    fun `should navigate back to previous screen using popTo`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            navigator.pushNew(RootDestinationConfig.ShowDetails(ShowDetailsParam(1)))
            awaitItem().active.instance.shouldBeInstanceOf<ShowDetailsDestination>()

            navigator.pushNew(RootDestinationConfig.MoreShows(1))
            awaitItem().active.instance.shouldBeInstanceOf<MoreShowsDestination>()

            navigator.popTo(0)

            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()
        }
    }

    @Test
    fun `should return default notification permission state`() = runTest {
        presenter.notificationPermissionState.test {
            awaitItem() shouldBe NotificationPermissionState()
        }
    }

    @Test
    fun `should set requestPermission given rationale accepted`() = runTest {
        presenter.notificationPermissionState.test {
            awaitItem() shouldBe NotificationPermissionState()

            datastoreRepository.setShowNotificationRationale(true)
            awaitItem() shouldBe NotificationPermissionState(showRationale = true)

            presenter.onRationaleAccepted()
            awaitItem() shouldBe NotificationPermissionState(
                showRationale = false,
                requestPermission = true,
            )
        }
    }

    @Test
    fun `should not mark permission as asked given sheet dismissed without interaction`() = runTest {
        presenter.notificationPermissionState.test {
            awaitItem() shouldBe NotificationPermissionState()

            datastoreRepository.setShowNotificationRationale(true)
            awaitItem() shouldBe NotificationPermissionState(showRationale = true)

            presenter.onRationaleDismissed()
            awaitItem() shouldBe NotificationPermissionState(showRationale = false)
        }
    }

    @Test
    fun `should show rationale again given sheet was dismissed without interaction`() = runTest {
        presenter.notificationPermissionState.test {
            awaitItem() shouldBe NotificationPermissionState()

            datastoreRepository.setShowNotificationRationale(true)
            awaitItem() shouldBe NotificationPermissionState(showRationale = true)

            presenter.onRationaleDismissed()
            awaitItem() shouldBe NotificationPermissionState(showRationale = false)

            datastoreRepository.setShowNotificationRationale(true)
            awaitItem() shouldBe NotificationPermissionState(showRationale = true)
        }
    }

    @Test
    fun `should enable notifications given permission granted`() = runTest {
        presenter.notificationPermissionState.test {
            awaitItem() shouldBe NotificationPermissionState()

            datastoreRepository.setRequestNotificationPermission(true)
            awaitItem() shouldBe NotificationPermissionState(requestPermission = true)

            presenter.onNotificationPermissionResult(granted = true)
            awaitItem() shouldBe NotificationPermissionState(requestPermission = false)
        }
    }

    @Test
    fun `should navigate to Debug given DebugMenu deep link`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<HomeDestination>()

            presenter.onDeepLink(DeepLinkDestination.DebugMenu)

            awaitItem().active.instance.shouldBeInstanceOf<DebugDestination>()
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
