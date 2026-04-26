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
import com.thomaskioko.tvmaniac.domain.theme.Theme
import com.thomaskioko.tvmaniac.genreshows.nav.GenreShowsRoute
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

abstract class DefaultRootPresenterTest {
    abstract val rootPresenterFactory: RootPresenter.Factory
    abstract val datastoreRepository: DatastoreRepository
    abstract val navigator: Navigator

    private val lifecycle = LifecycleRegistry()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var presenter: RootPresenter

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        lifecycle.resume()

        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        presenter = rootPresenterFactory(componentContext)
    }

    @AfterTest
    fun tearDown() {
        lifecycle.destroy()
    }

    @Test
    fun `initial state should be Home`() = runTest {
        presenter.childStack.test { awaitItem().active.instance.shouldBeInstanceOf<RootChild>() }
    }

    @Test
    fun `should return Home as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.bringToFront(ShowDetailsRoute(ShowDetailsParam(1)))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<RootChild>()

            navigator.pop()

            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return ShowDetails as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.bringToFront(ShowDetailsRoute(ShowDetailsParam(1)))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return MoreShows as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.bringToFront(MoreShowsRoute(1))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return SeasonDetails as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            val param = SeasonDetailsUiParam(
                showTraktId = 1,
                seasonId = 2,
                seasonNumber = 3,
            )
            navigator.bringToFront(SeasonDetailsRoute(param))

            val seasonDetailsScreen = awaitItem().active.instance

            seasonDetailsScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return Trailers as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.bringToFront(TrailersRoute(1))

            val trailersScreen = awaitItem().active.instance

            trailersScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return GenreShows as active instance`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.bringToFront(GenreShowsRoute(1))

            val genreShowsScreen = awaitItem().active.instance

            genreShowsScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should navigate to ShowDetails using pushNew`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.pushNew(ShowDetailsRoute(ShowDetailsParam(1)))

            val showDetailsScreen = awaitItem().active.instance

            showDetailsScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should navigate to ShowDetails using pushToFront`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.pushToFront(ShowDetailsRoute(ShowDetailsParam(1)))

            val showDetailsScreen = awaitItem().active.instance

            showDetailsScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should navigate back to previous screen using popTo`() = runTest {
        presenter.childStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.pushNew(ShowDetailsRoute(ShowDetailsParam(1)))
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.pushNew(MoreShowsRoute(1))
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.popTo(0)

            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()
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
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            presenter.onDeepLink(DeepLinkDestination.DebugMenu)

            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()
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
                    appTheme = Theme.DARK_THEME,
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
                    appTheme = Theme.LIGHT_THEME,
                )
        }
    }
}
