package com.thomaskioko.tvmaniac.presenter.root

import app.cash.turbine.test
import com.thomaskioko.root.model.AppUiState
import com.thomaskioko.tvmaniac.core.connectivity.testing.FakeInternetConnectionChecker
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashlyticsConfiguration
import com.thomaskioko.tvmaniac.datastore.api.DatastoreRepository
import com.thomaskioko.tvmaniac.domain.theme.Theme
import com.thomaskioko.tvmaniac.featureflags.testing.FakeRemoteConfigBridge
import com.thomaskioko.tvmaniac.navigation.Navigator
import com.thomaskioko.tvmaniac.syncstate.api.SyncObserver
import com.thomaskioko.tvmaniac.testing.di.TestGraph
import dev.zacsweers.metro.createGraphFactory
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

internal class DefaultRootPresenterIosTest : DefaultRootPresenterTest() {
    private val testGraph: TestGraph by lazy {
        createGraphFactory<TestGraph.Factory>().create(
            remoteConfigBridge = FakeRemoteConfigBridge(),
            crashlyticsConfiguration = FakeCrashlyticsConfiguration(isConfigured = false),
        )
    }

    override val rootPresenterFactory: RootPresenter.Factory
        get() = testGraph.rootPresenterFactory

    override val datastoreRepository: DatastoreRepository
        get() = testGraph.datastoreRepository

    override val navigator: Navigator
        get() = testGraph.navigator

    override val syncObserver: SyncObserver
        get() = testGraph.syncObserver

    override val internetConnectionChecker: FakeInternetConnectionChecker
        get() = testGraph.fakeInternetConnectionChecker

    @Test
    fun `should enable liquid glass given the flag is turned on`() = runTest(testDispatcher) {
        presenter.appUiState.test {
            awaitItem() shouldBe AppUiState()
            awaitItem() shouldBe AppUiState(isFetching = false, appTheme = Theme.SYSTEM_THEME)

            testGraph.fakeFeatureFlagLocalStore.set("enable_liquid_glass", true)

            awaitItem() shouldBe AppUiState(isFetching = false, appTheme = Theme.SYSTEM_THEME, liquidGlassEnabled = true)
        }
    }
}
