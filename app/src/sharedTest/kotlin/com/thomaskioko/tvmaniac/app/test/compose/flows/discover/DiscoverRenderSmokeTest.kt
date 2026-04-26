package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import org.junit.Before
import org.junit.Test

internal class DiscoverRenderSmokeTest : BaseAppFlowTest() {

    @Before
    fun stubEndpoints() {
        scenarios.stubDiscoverBrowse()
    }

    @Test
    fun shouldRenderTrendingShowCardFromRealPipeline() {
        discoverRobot.verifyShowCardIsShown(traktId = 1388L)
    }
}
