package com.thomaskioko.tvmaniac.featureflags.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagFetchInterval
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.testing.FakeRemoteConfigBridge
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class IosDebugFeatureFlagsTest {

    @Test
    fun `should pass through production when debug is false`() = runTest {
        val bridge = FakeRemoteConfigBridge().also {
            it.setValue(FeatureFlag.SIMKL_LOGIN_ENABLED.key, true)
        }
        val production = buildProduction(bridge).also {
            it.setup()
            it.refresh()
        }
        val localStore = FakeFeatureFlagLocalStore().also {
            it.set(FeatureFlag.SIMKL_LOGIN_ENABLED, false)
        }
        val wrapper = IosDebugFeatureFlags(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = false),
        )

        wrapper.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
        wrapper.source(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should let local value win over remote in debug`() = runTest {
        val bridge = FakeRemoteConfigBridge().also {
            it.setValue(FeatureFlag.SIMKL_LOGIN_ENABLED.key, true)
        }
        val production = buildProduction(bridge).also {
            it.setup()
            it.refresh()
        }
        val localStore = FakeFeatureFlagLocalStore().also {
            it.set(FeatureFlag.SIMKL_LOGIN_ENABLED, false)
        }
        val wrapper = IosDebugFeatureFlags(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should flip source to Local when local value is set in debug`() = runTest {
        val bridge = FakeRemoteConfigBridge()
        val production = buildProduction(bridge).also { it.setup() }
        val localStore = FakeFeatureFlagLocalStore()
        val wrapper = IosDebugFeatureFlags(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.source(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            localStore.set(FeatureFlag.SIMKL_LOGIN_ENABLED, true)
            awaitItem() shouldBe FeatureFlagSource.Local
        }
    }

    @Test
    fun `should revert to remote when local value is cleared in debug`() = runTest {
        val bridge = FakeRemoteConfigBridge().also {
            it.setValue(FeatureFlag.SIMKL_LOGIN_ENABLED.key, true)
        }
        val production = buildProduction(bridge).also {
            it.setup()
            it.refresh()
        }
        val localStore = FakeFeatureFlagLocalStore().also {
            it.set(FeatureFlag.SIMKL_LOGIN_ENABLED, false)
        }
        val wrapper = IosDebugFeatureFlags(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe false
            localStore.clear(FeatureFlag.SIMKL_LOGIN_ENABLED)
            awaitItem() shouldBe true
        }
    }

    @Test
    fun `should forward refresh to production`() = runTest {
        val bridge = FakeRemoteConfigBridge().also {
            it.setValue(FeatureFlag.SIMKL_LOGIN_ENABLED.key, true)
        }
        val production = buildProduction(bridge).also { it.setup() }
        val wrapper = IosDebugFeatureFlags(
            production = production,
            localStore = FakeFeatureFlagLocalStore(),
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.refresh()

        wrapper.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun buildProduction(bridge: FakeRemoteConfigBridge): IosRemoteConfigFeatureFlags =
        IosRemoteConfigFeatureFlags(
            bridge = bridge,
            fetchInterval = FeatureFlagFetchInterval(seconds = 900L),
            state = FeatureFlagsState(),
            logger = FakeLogger(),
        )

    private class StubDebugConfig(override val isDebug: Boolean) : DebugConfig
}
