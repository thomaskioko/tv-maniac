package com.thomaskioko.tvmaniac.iosframework

import com.thomaskioko.tvmaniac.appconfig.Platform
import com.thomaskioko.tvmaniac.featureflags.RemoteConfigBridge
import dev.zacsweers.metro.createGraphFactory
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IosApplicationGraphTest {

    @Test
    fun `should return debugBuild true when factory receives isDebug true`() {
        val graph = createGraphFactory<IosApplicationGraph.Factory>().create(
            isDebug = true,
            remoteConfigBridge = FakeRemoteConfigBridge,
        )

        graph.debugConfig.isDebug shouldBe true
    }

    @Test
    fun `should return debugBuild false when factory receives isDebug false`() {
        val graph = createGraphFactory<IosApplicationGraph.Factory>().create(
            isDebug = false,
            remoteConfigBridge = FakeRemoteConfigBridge,
        )

        graph.debugConfig.isDebug shouldBe false
    }

    @Test
    fun `should return Platform IOS`() {
        val graph = createGraphFactory<IosApplicationGraph.Factory>().create(
            isDebug = false,
            remoteConfigBridge = FakeRemoteConfigBridge,
        )

        graph.appMetadata.platform shouldBe Platform.IOS
    }

    private object FakeRemoteConfigBridge : RemoteConfigBridge {
        override fun setMinimumFetchIntervalSeconds(seconds: Long): Unit = Unit
        override fun fetchAndActivate(onResult: (Boolean) -> Unit): Unit = onResult(false)
        override fun getBoolean(key: String): Boolean = false
        override fun setDefaults(defaults: Map<String, Boolean>): Unit = Unit
        override fun addOnConfigUpdateListener(onUpdate: () -> Unit): Unit = Unit
    }
}
