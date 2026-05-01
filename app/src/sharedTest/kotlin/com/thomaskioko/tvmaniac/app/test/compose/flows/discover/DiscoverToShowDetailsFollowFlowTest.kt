package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Before
import org.junit.Test

internal class DiscoverToShowDetailsFollowFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L

    @Before
    fun stubEndpoints() {
        scenarios.discover.stubBrowseGraph()
    }

    @Test
    fun givenShow_whenTrackIsClicked_thenPersistsInFollowedShows() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertTrackButtonDisplayed()
        showDetailsRobot.clickTrackButton()
        showDetailsRobot.assertStopTrackingButtonDisplayed()
        rootRobot.dismissNotificationRationale()
    }

    @Test
    fun givenShowDetails_whenBackIsPressed_thenRestoresDiscover() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertTrackButtonDisplayed()
        showDetailsRobot.pressBack()
        showDetailsRobot.assertDoesNotExist("show_details_track_button")
        discoverRobot.assertShowCardDisplayed(breakingBadTraktId)
    }
}
