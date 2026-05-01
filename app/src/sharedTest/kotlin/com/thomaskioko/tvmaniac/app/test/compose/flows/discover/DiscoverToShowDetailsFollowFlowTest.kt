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
    fun shouldFollowShowAndPersistItInTheFollowedShowsTable() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyTrackButtonIsShown()
        showDetailsRobot.clickTrackButton()
        showDetailsRobot.verifyStopTrackingButtonIsShown()
        rootRobot.verifyNotificationRationaleIsShownAndDismissed()
    }

    @Test
    fun shouldPopShowDetailsAndRestoreDiscoverOnBackPress() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyTrackButtonIsShown()
        showDetailsRobot.pressBack()
        showDetailsRobot.verifyTagHidden("show_details_track_button")
        discoverRobot.verifyShowCardIsShown(breakingBadTraktId)
    }
}
