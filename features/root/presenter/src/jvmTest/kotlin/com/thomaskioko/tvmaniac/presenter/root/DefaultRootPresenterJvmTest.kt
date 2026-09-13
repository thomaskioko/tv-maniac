package com.thomaskioko.tvmaniac.presenter.root

import app.cash.turbine.test
import com.thomaskioko.root.model.AppUiState
import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.theme.Theme
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.testing.di.TestGraph
import dev.zacsweers.metro.createGraphFactory
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultRootPresenterJvmTest : DefaultRootPresenterTest() {
    private val testComponent: TestGraph by lazy {
        createGraphFactory<TestGraph.Factory>().create()
    }

    override val rootPresenterFactory: RootPresenter.Factory
        get() = testComponent.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testComponent.datastoreRepository

    override val navigator: Navigator
        get() = testComponent.navigator

    override val syncObserver: SyncObserver
        get() = testComponent.syncObserver

    override val internetConnectionChecker: FakeInternetConnectionChecker
        get() = testComponent.fakeInternetConnectionChecker

    @Test
    fun `should keep liquid glass disabled given the flag is at its default`() = runTest(testDispatcher) {
        presenter.appUiState.test {
            awaitItem() shouldBe AppUiState()
            awaitItem() shouldBe AppUiState(isFetching = false, appTheme = Theme.SYSTEM_THEME, liquidGlassEnabled = false)
        }
    }

    @Test
    fun `should enable liquid glass given the flag is turned on`() = runTest(testDispatcher) {
        presenter.appUiState.test {
            awaitItem() shouldBe AppUiState()
            awaitItem() shouldBe AppUiState(isFetching = false, appTheme = Theme.SYSTEM_THEME)

            testComponent.fakeLiquidGlassAvailability.setEnabled(true)

            awaitItem() shouldBe AppUiState(isFetching = false, appTheme = Theme.SYSTEM_THEME, liquidGlassEnabled = true)
        }
    }
}
