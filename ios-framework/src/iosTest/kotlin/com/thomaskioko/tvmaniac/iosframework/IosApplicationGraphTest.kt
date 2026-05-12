package com.thomaskioko.tvmaniac.iosframework

import com.thomaskioko.tvmaniac.appconfig.Platform
import dev.zacsweers.metro.createGraphFactory
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class IosApplicationGraphTest {

    @Test
    fun `should return debugBuild true when factory receives isDebug true`() {
        val graph = createGraphFactory<IosApplicationGraph.Factory>().create(isDebug = true)

        graph.applicationInfo.debugBuild shouldBe true
    }

    @Test
    fun `should return debugBuild false when factory receives isDebug false`() {
        val graph = createGraphFactory<IosApplicationGraph.Factory>().create(isDebug = false)

        graph.applicationInfo.debugBuild shouldBe false
    }

    @Test
    fun `should return Platform IOS`() {
        val graph = createGraphFactory<IosApplicationGraph.Factory>().create(isDebug = false)

        graph.applicationInfo.platform shouldBe Platform.IOS
    }
}
