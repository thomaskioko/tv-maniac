package com.thomaskioko.tvmaniac.featureflags.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagFetchInterval
import com.thomaskioko.tvmaniac.featureflags.testing.FakeRemoteConfigBridge
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class IosRemoteConfigFeatureFlagsTest {

    @Test
    fun `should apply minimum fetch interval given setup`() = runTest {
        val bridge = FakeRemoteConfigBridge()
        val featureFlags = build(bridge, FeatureFlagFetchInterval(seconds = 900L))

        featureFlags.setup()

        bridge.lastMinimumFetchIntervalSeconds shouldBe 900L
    }

    @Test
    fun `should publish enum default given setup and no remote value`() = runTest {
        val bridge = FakeRemoteConfigBridge()
        val featureFlags = build(bridge)

        featureFlags.setup()
        featureFlags.refresh()

        featureFlags.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should publish remote value given successful refresh`() = runTest {
        val bridge = FakeRemoteConfigBridge().also { it.setValue(FeatureFlag.SIMKL_LOGIN_ENABLED.key, true) }
        val featureFlags = build(bridge)

        featureFlags.setup()
        featureFlags.refresh()

        featureFlags.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should publish cached value even when fetch fails`() = runTest {
        val bridge = FakeRemoteConfigBridge().also {
            it.setValue(FeatureFlag.SIMKL_LOGIN_ENABLED.key, true)
            it.setFetchResult(success = false)
        }
        val featureFlags = build(bridge)

        featureFlags.setup()
        featureFlags.refresh()

        featureFlags.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should publish updated value given realtime listener fires after setup`() = runTest {
        val bridge = FakeRemoteConfigBridge()
        val featureFlags = build(bridge)
        featureFlags.setup()

        featureFlags.isEnabled(FeatureFlag.SIMKL_LOGIN_ENABLED).test {
            awaitItem() shouldBe false

            bridge.setValue(FeatureFlag.SIMKL_LOGIN_ENABLED.key, true)
            bridge.triggerConfigUpdate()

            awaitItem() shouldBe true
        }
    }

    private fun build(
        bridge: FakeRemoteConfigBridge,
        fetchInterval: FeatureFlagFetchInterval = FeatureFlagFetchInterval(seconds = 900L),
    ): IosRemoteConfigFeatureFlags = IosRemoteConfigFeatureFlags(
        bridge = bridge,
        fetchInterval = fetchInterval,
        state = FeatureFlagsState(),
        logger = FakeLogger(),
    )
}
