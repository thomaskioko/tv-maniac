package com.thomaskioko.tvmaniac.featureflags.implementation

import app.cash.turbine.test
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeLogger
import com.thomaskioko.tvmaniac.featureflags.FeatureFlag
import com.thomaskioko.tvmaniac.featureflags.FeatureFlagsRemoteConfig
import com.thomaskioko.tvmaniac.featureflags.RemoteFlag
import com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagFetchInterval
import com.thomaskioko.tvmaniac.featureflags.testing.FakeRemoteConfigBridge
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test

class IosRemoteConfigTest {

    private val flagKey = "simkl_login_enabled"
    private val flag: FeatureFlag<Boolean> = TestRemoteFlag(flagKey)

    @Test
    fun `should apply minimum fetch interval given setup`() = runTest {
        val bridge = FakeRemoteConfigBridge()
        val remote = build(bridge, FeatureFlagFetchInterval(seconds = 900L))

        remote.setup()

        bridge.lastMinimumFetchIntervalSeconds shouldBe 900L
    }

    @Test
    fun `should publish default given setup and no remote value`() = runTest {
        val bridge = FakeRemoteConfigBridge()
        val remote = build(bridge)

        remote.setup()
        remote.refresh()

        remote.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe false
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should publish remote value given successful refresh`() = runTest {
        val bridge = FakeRemoteConfigBridge().also { it.setValue(flagKey, true) }
        val remote = build(bridge)

        remote.setup()
        remote.refresh()

        remote.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should publish cached value even when fetch fails`() = runTest {
        val bridge = FakeRemoteConfigBridge().also {
            it.setValue(flagKey, true)
            it.setFetchResult(success = false)
        }
        val remote = build(bridge)

        remote.setup()
        remote.refresh()

        remote.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should publish updated value given realtime listener fires after setup`() = runTest {
        val bridge = FakeRemoteConfigBridge()
        val remote = build(bridge)
        remote.setup()

        remote.observeBoolean(flagKey, default = false).test {
            awaitItem() shouldBe false

            bridge.setValue(flagKey, true)
            bridge.triggerConfigUpdate()

            awaitItem() shouldBe true
        }
    }

    private fun build(
        bridge: FakeRemoteConfigBridge,
        fetchInterval: FeatureFlagFetchInterval = FeatureFlagFetchInterval(seconds = 900L),
    ): IosRemoteConfig = IosRemoteConfig(
        bridge = bridge,
        fetchInterval = fetchInterval,
        state = RemoteConfigState(),
        flags = lazyOf(setOf(flag)),
        logger = FakeLogger(),
    )

    private class TestRemoteFlag(key: String) : RemoteFlag(
        key = key,
        title = "Test",
        description = "Test flag.",
        dateAdded = LocalDate(2026, 1, 1),
        defaultValue = false,
        remote = NoOpRemoteConfig,
    )

    private object NoOpRemoteConfig : FeatureFlagsRemoteConfig {
        override fun observeBoolean(key: String, default: Boolean) = kotlinx.coroutines.flow.flowOf(default)
        override fun observeSource(key: String) = kotlinx.coroutines.flow.flowOf(
            com.thomaskioko.tvmaniac.featureflags.model.FeatureFlagSource.Firebase,
        )
        override suspend fun refresh() = Unit
        override suspend fun setDefaults(defaults: Map<String, Boolean>) = Unit
    }
}
