package com.thomaskioko.tvmaniac.featureflags.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.appconfig.DebugConfig
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagFetchInterval
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource
import com.thomaskioko.tvmaniac.featureflags.testing.FakeFeatureFlagLocalStore
import com.thomaskioko.tvmaniac.featureflags.testing.FakeRemoteConfigBridge
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class IosDebugRemoteConfigTest {

    private val flagKey = "simkl_login_enabled"

    @Test
    fun `should pass through production when debug is false`() = runTest {
        val bridge = FakeRemoteConfigBridge().also { it.setValue(flagKey, true) }
        val production = buildProduction(bridge).also {
            it.setup()
            it.refresh()
        }
        val localStore = FakeFeatureFlagLocalStore().also { it.set(flagKey, false) }
        val wrapper = IosDebugRemoteConfig(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = false),
        )

        wrapper.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
        wrapper.observeSource(flagKey).test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should let local value win over remote in debug`() = runTest {
        val bridge = FakeRemoteConfigBridge().also { it.setValue(flagKey, true) }
        val production = buildProduction(bridge).also {
            it.setup()
            it.refresh()
        }
        val localStore = FakeFeatureFlagLocalStore().also { it.set(flagKey, false) }
        val wrapper = IosDebugRemoteConfig(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should flip source to Local when local value is set in debug`() = runTest {
        val bridge = FakeRemoteConfigBridge()
        val production = buildProduction(bridge).also { it.setup() }
        val localStore = FakeFeatureFlagLocalStore()
        val wrapper = IosDebugRemoteConfig(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.observeSource(flagKey).test {
            awaitItem() shouldBe FeatureFlagSource.Firebase
            localStore.set(flagKey, true)
            awaitItem() shouldBe FeatureFlagSource.Local
        }
    }

    @Test
    fun `should revert to remote when local value is cleared in debug`() = runTest {
        val bridge = FakeRemoteConfigBridge().also { it.setValue(flagKey, true) }
        val production = buildProduction(bridge).also {
            it.setup()
            it.refresh()
        }
        val localStore = FakeFeatureFlagLocalStore().also { it.set(flagKey, false) }
        val wrapper = IosDebugRemoteConfig(
            production = production,
            localStore = localStore,
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe false
            localStore.clear(flagKey)
            awaitItem() shouldBe true
        }
    }

    @Test
    fun `should forward refresh to production`() = runTest {
        val bridge = FakeRemoteConfigBridge().also { it.setValue(flagKey, true) }
        val production = buildProduction(bridge).also { it.setup() }
        val wrapper = IosDebugRemoteConfig(
            production = production,
            localStore = FakeFeatureFlagLocalStore(),
            debugConfig = StubDebugConfig(isDebug = true),
        )

        wrapper.refresh()

        wrapper.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun buildProduction(bridge: FakeRemoteConfigBridge): IosRemoteConfig =
        IosRemoteConfig(
            bridge = bridge,
            fetchInterval = FeatureFlagFetchInterval(seconds = 900L),
            state = RemoteConfigState(),
            flags = lazyOf(emptySet()),
            logger = FakeLogger(),
        )

    private class StubDebugConfig(override val isDebug: Boolean) : DebugConfig
}
