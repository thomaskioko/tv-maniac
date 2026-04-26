package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import org.junit.Before
import kotlin.test.Test

internal class DiscoverRenderSmokeTest : BaseAppRobolectricTest() {

    @Before
    fun stubEndpoints() {
        scenarios.stubDiscoverBrowse()
    }

    @Test
    fun `should render trending show card from real pipeline`() {
        discoverRobot.verifyShowCardIsShown(traktId = 1388L)
    }
}
