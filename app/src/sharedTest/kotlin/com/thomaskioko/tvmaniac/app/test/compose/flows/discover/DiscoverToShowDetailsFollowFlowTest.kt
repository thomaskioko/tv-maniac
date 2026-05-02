package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class DiscoverToShowDetailsFollowFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L

    @Test
    fun givenShow_whenTrackIsClicked_thenPersistsInFollowedShows() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
    }

    @Test
    fun givenShowDetails_whenBackIsPressed_thenRestoresDiscover() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertTrackButtonDisplayed()
        rootRobot.pressBack()
        showDetailsRobot.assertDoesNotExist("show_details_track_button")
        discoverRobot.assertShowCardDisplayed(breakingBadTraktId)
    }
}
