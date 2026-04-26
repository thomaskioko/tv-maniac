package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.Test

internal class DiscoverToShowDetailsFollowFlowTest : BaseAppRobolectricTest() {

    private val breakingBadTraktId = 1388L

    @Before
    fun stubEndpoints() {
        scenarios.stubShowDetailsBrowse(traktShowId = breakingBadTraktId)
    }

    @Test
    fun `should follow show and persist it in the followed shows table`() = runTest {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyTrackButtonIsShown()
        showDetailsRobot.clickTrackButton()
        showDetailsRobot.verifyStopTrackingButtonIsShown()
    }

    @Test
    fun `should pop show details and restore discover on back press`() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyTrackButtonIsShown()
        showDetailsRobot.pressBack()
        showDetailsRobot.verifyTagHidden("show_details_track_button")
        discoverRobot.verifyShowCardIsShown(breakingBadTraktId)
    }
}
