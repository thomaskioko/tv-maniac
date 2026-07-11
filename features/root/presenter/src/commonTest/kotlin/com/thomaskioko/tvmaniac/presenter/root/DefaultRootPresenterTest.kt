package com.thomaskioko.tvmaniac.presenter.root

import app.cash.turbine.test
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.thomaskioko.root.model.AppUiState
import com.thomaskioko.root.model.DeepLinkDestination
import com.thomaskioko.root.model.NotificationPermissionState
import com.thomaskioko.tvmaniac.datastore.api.AppTheme
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.theme.Theme
import com.thomaskioko.tvmaniac.i18n.StringResourceKey
import com.thomaskioko.tvmaniac.i18n.testing.util.getString
import com.thomaskioko.tvmaniac.moreshows.nav.MoreShowsRoute
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.presenter.root.model.ToastState
import com.thomaskioko.tvmaniac.presenter.root.model.ToastType
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsRoute
import com.thomaskioko.tvmaniac.seasondetails.nav.SeasonDetailsUiParam
import com.thomaskioko.tvmaniac.showdetails.nav.ShowDetailsRoute
import com.thomaskioko.tvmaniac.showdetails.nav.model.ShowDetailsParam
import com.thomaskioko.tvmaniac.syncstate.api.SyncError
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.trailers.nav.TrailersRoute
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
abstract class DefaultRootPresenterTest {
    abstract val rootPresenterFactory: RootPresenter.Factory
    abstract val datastoreRepository: DatastoreRepository
    abstract val navigator: Navigator
    abstract val syncObserver: SyncObserver

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
    fun `initial active item should be the Discover tab body`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test { awaitItem().active.instance.shouldBeInstanceOf<RootChild>() }
    }

    @Test
    fun `should return Home as active instance`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.bringToFront(ShowDetailsRoute(ShowDetailsParam(1)))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<RootChild>()

            navigator.navigateBack()

            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return ShowDetails as active instance`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.bringToFront(ShowDetailsRoute(ShowDetailsParam(1)))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return MoreShows as active instance`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.bringToFront(MoreShowsRoute(1))

            val moreScreen = awaitItem().active.instance

            moreScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return SeasonDetails as active instance`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            val param = SeasonDetailsUiParam(
                showId = 1,
                seasonId = 2,
                seasonNumber = 3,
            )
            navigator.bringToFront(SeasonDetailsRoute(param))

            val seasonDetailsScreen = awaitItem().active.instance

            seasonDetailsScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return Trailers as active instance`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.bringToFront(TrailersRoute(1))

            val trailersScreen = awaitItem().active.instance

            trailersScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should navigate to ShowDetails using pushNew`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(1)))

            val showDetailsScreen = awaitItem().active.instance

            showDetailsScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should navigate to ShowDetails using pushToFront`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.pushToFront(ShowDetailsRoute(ShowDetailsParam(1)))

            val showDetailsScreen = awaitItem().active.instance

            showDetailsScreen.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should navigate back to tab root using popTo`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.navigateTo(ShowDetailsRoute(ShowDetailsParam(1)))
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.navigateTo(MoreShowsRoute(1))
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            navigator.popTo(toIndex = 0)

            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return default notification permission state`() = runTest(testDispatcher) {
        presenter.notificationPermissionState.test {
            awaitItem() shouldBe NotificationPermissionState()
        }
    }

    @Test
    fun `should set requestPermission given rationale accepted`() = runTest(testDispatcher) {
        presenter.notificationPermissionState.test {
            awaitItem() shouldBe NotificationPermissionState()

            datastoreRepository.setShowNotificationRationale(true)
            awaitItem() shouldBe NotificationPermissionState(showRationale = true)

            presenter.onRationaleAccepted()
            awaitItem() shouldBe NotificationPermissionState(showRationale = false)
            awaitItem() shouldBe NotificationPermissionState(
                showRationale = false,
                requestPermission = true,
            )
        }
    }

    @Test
    fun `should not mark permission as asked given sheet dismissed without interaction`() = runTest(testDispatcher) {
        presenter.notificationPermissionState.test {
            awaitItem() shouldBe NotificationPermissionState()

            datastoreRepository.setShowNotificationRationale(true)
            awaitItem() shouldBe NotificationPermissionState(showRationale = true)

            presenter.onRationaleDismissed()
            awaitItem() shouldBe NotificationPermissionState(showRationale = false)
        }
    }

    @Test
    fun `should show rationale again given sheet was dismissed without interaction`() = runTest(testDispatcher) {
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
    fun `should enable notifications given permission granted`() = runTest(testDispatcher) {
        presenter.notificationPermissionState.test {
            awaitItem() shouldBe NotificationPermissionState()

            datastoreRepository.setRequestNotificationPermission(true)
            awaitItem() shouldBe NotificationPermissionState(requestPermission = true)

            presenter.onNotificationPermissionResult(granted = true)
            awaitItem() shouldBe NotificationPermissionState(requestPermission = false)
        }
    }

    @Test
    fun `should navigate to Debug given DebugMenu deep link`() = runTest(testDispatcher) {
        presenter.homePresenter.discoverChildStack.test {
            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()

            presenter.onDeepLink(DeepLinkDestination.DebugMenu)

            awaitItem().active.instance.shouldBeInstanceOf<RootChild>()
        }
    }

    @Test
    fun `should return initial theme state`() = runTest(testDispatcher) {
        presenter.appUiState.value shouldBe AppUiState()
    }

    @Test
    fun `should update theme to Dark when DarkTheme is set`() = runTest(testDispatcher) {
        presenter.appUiState.test {
            awaitItem() shouldBe AppUiState()
            awaitItem() shouldBe AppUiState(isFetching = false, appTheme = Theme.SYSTEM_THEME)

            datastoreRepository.saveTheme(AppTheme.DARK_THEME)

            awaitItem() shouldBe
                AppUiState(
                    isFetching = false,
                    appTheme = Theme.DARK_THEME,
                )
        }
    }

    @Test
    fun `should update theme to Light when LightTheme is set`() = runTest(testDispatcher) {
        presenter.appUiState.test {
            awaitItem() shouldBe AppUiState()
            awaitItem() shouldBe AppUiState(isFetching = false, appTheme = Theme.SYSTEM_THEME)

            datastoreRepository.saveTheme(AppTheme.LIGHT_THEME)

            awaitItem() shouldBe
                AppUiState(
                    isFetching = false,
                    appTheme = Theme.LIGHT_THEME,
                )
        }
    }

    @Test
    fun `should expose empty ToastState given idle observer`() = runTest(testDispatcher) {
        presenter.toastState.value shouldBe ToastState()
    }

    @Test
    fun `should hide sync indicator given idle observer`() = runTest(testDispatcher) {
        presenter.syncIndicatorVisible.value shouldBe false
    }

    @Test
    fun `should show sync indicator given sync is running`() = runTest(testDispatcher) {
        presenter.syncIndicatorVisible.test {
            awaitItem() shouldBe false

            val gate = CompletableDeferred<Unit>()
            val syncJob = launch { syncObserver.trackSync("test") { gate.await() } }

            awaitItem() shouldBe true

            gate.complete(Unit)
            syncJob.join()
        }
    }

    @Test
    fun `should hold sync indicator for MIN_STATUS_DISPLAY given sync completes quickly`() = runTest(testDispatcher) {
        presenter.syncIndicatorVisible.test {
            awaitItem() shouldBe false

            val gate = CompletableDeferred<Unit>()
            val syncJob = launch { syncObserver.trackSync("test") { gate.await() } }
            awaitItem() shouldBe true

            gate.complete(Unit)
            syncJob.join()
            runCurrent()
            expectNoEvents()

            advanceTimeBy(1600.milliseconds)
            runCurrent()

            awaitItem() shouldBe false
        }
    }

    @Test
    fun `should not emit toast given sync is running without errors`() = runTest(testDispatcher) {
        presenter.toastState.test {
            awaitItem() shouldBe ToastState()

            val gate = CompletableDeferred<Unit>()
            val syncJob = launch { syncObserver.trackSync("test") { gate.await() } }
            runCurrent()

            expectNoEvents()
            presenter.toastState.value shouldBe ToastState()

            gate.complete(Unit)
            syncJob.join()
        }
    }

    @Test
    fun `should expose error toast given SyncErrorChannel logs an error`() = runTest(testDispatcher) {
        presenter.toastState.test {
            awaitItem() shouldBe ToastState()

            syncObserver.log(SyncError.MarkWatchedFailed(showId = 1L, cause = RuntimeException("boom")))

            val emitted = awaitItem()
            emitted.message shouldBe StringResourceKey.SyncFailedWillRetry.getString()
            emitted.type shouldBe ToastType.Error
            emitted.persistent shouldBe false
            (emitted.id != null) shouldBe true
        }
    }

    @Test
    fun `should expose error toast given error logged while sync is running`() = runTest(testDispatcher) {
        presenter.toastState.test {
            awaitItem() shouldBe ToastState()

            val gate = CompletableDeferred<Unit>()
            val syncJob = launch { syncObserver.trackSync("test") { gate.await() } }

            syncObserver.log(SyncError.MarkWatchedFailed(showId = 1L, cause = RuntimeException("boom")))
            awaitItem().type shouldBe ToastType.Error

            gate.complete(Unit)
            syncJob.join()
        }
    }

    @Test
    fun `should clear error toast given onToastShown while sync is running`() = runTest(testDispatcher) {
        presenter.toastState.test {
            awaitItem() shouldBe ToastState()

            val gate = CompletableDeferred<Unit>()
            val syncJob = launch { syncObserver.trackSync("test") { gate.await() } }

            syncObserver.log(SyncError.MarkWatchedFailed(showId = 1L, cause = RuntimeException("boom")))
            val errorToast = awaitItem()
            errorToast.type shouldBe ToastType.Error

            presenter.onToastShown(errorToast.id!!)
            awaitItem() shouldBe ToastState()

            gate.complete(Unit)
            syncJob.join()
        }
    }

    @Test
    fun `should expose error toast given trackSync block throws while syncing`() = runTest(testDispatcher) {
        presenter.toastState.test {
            awaitItem() shouldBe ToastState()

            val gate = CompletableDeferred<Unit>()
            val syncJob = launch {
                try {
                    syncObserver.trackSync("library-sync") {
                        gate.await()
                        throw RuntimeException("simulated 429")
                    }
                } catch (_: RuntimeException) {
                    // expected; the rethrow surfaces the failure to the dispatcher in production
                }
            }

            gate.complete(Unit)
            syncJob.join()

            val errorToast = awaitItem()
            errorToast.type shouldBe ToastType.Error
            errorToast.message shouldBe StringResourceKey.SyncFailedWillRetry.getString()
            errorToast.persistent shouldBe false
        }
    }

    @Test
    fun `should show account limit banner given limit error occurs`() = runTest(testDispatcher) {
        presenter.accountLimitBannerVisible.test {
            awaitItem() shouldBe false

            syncObserver.log(SyncError.AccountLimitExceeded(message = "limit", cause = RuntimeException("boom")))

            awaitItem() shouldBe true
        }
    }

    @Test
    fun `should keep account limit banner hidden for the session given limit error fires again after dismiss`() =
        runTest(testDispatcher) {
            presenter.accountLimitBannerVisible.test {
                awaitItem() shouldBe false

                syncObserver.log(SyncError.AccountLimitExceeded(message = "limit", cause = RuntimeException("boom")))
                awaitItem() shouldBe true

                presenter.onDismissAccountLimitBanner()
                awaitItem() shouldBe false

                syncObserver.log(SyncError.AccountLimitExceeded(message = "limit", cause = RuntimeException("boom")))
                runCurrent()

                expectNoEvents()
                presenter.accountLimitBannerVisible.value shouldBe false
            }
        }
}
