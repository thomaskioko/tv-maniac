package com.thomaskioko.tvmaniac.iosframework

import com.thomaskioko.tvmaniac.appconfig.Platform
import com.thomaskioko.tvmaniac.core.logger.fixture.FakeCrashlyticsConfiguration
import com.thomaskioko.tvmaniac.domain.widget.WidgetManager
import com.thomaskioko.tvmaniac.featureflags.testing.FakeRemoteConfigBridge
import dev.zacsweers.metro.createGraphFactory
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IosApplicationGraphTest {

    @Test
    fun `should return debugBuild true when factory receives isDebug true`() {
        val graph = createGraph(isDebug = true)

        graph.debugConfig.isDebug shouldBe true
    }

    @Test
    fun `should return debugBuild false when factory receives isDebug false`() {
        val graph = createGraph(isDebug = false)

        graph.debugConfig.isDebug shouldBe false
    }

    @Test
    fun `should return Platform IOS`() {
        val graph = createGraph(isDebug = false)

        graph.appMetadata.platform shouldBe Platform.IOS
    }

    private fun createGraph(isDebug: Boolean): IosApplicationGraph =
        createGraphFactory<IosApplicationGraph.Factory>().create(
            isDebug = isDebug,
            remoteConfigBridge = FakeRemoteConfigBridge(),
            widgetManager = FakeWidgetManager,
            crashlyticsConfiguration = FakeCrashlyticsConfiguration(isConfigured = false),
        )

    private object FakeWidgetManager : WidgetManager {
        override fun hasInstalledWidgets(onResult: (Boolean) -> Unit): Unit = onResult(false)
        override fun containerPath(): String? = null
        override fun reloadTimelines(): Unit = Unit
    }
}
