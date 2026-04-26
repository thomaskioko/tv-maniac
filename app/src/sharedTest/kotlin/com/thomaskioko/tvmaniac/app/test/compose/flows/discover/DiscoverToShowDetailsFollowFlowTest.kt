package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class DiscoverToShowDetailsFollowFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L

    @Before
    fun stubEndpoints() {
        scenarios.stubShowDetailsBrowse(traktShowId = breakingBadTraktId)
    }

    @Test
    fun shouldFollowShowAndPersistItInTheFollowedShowsTable() = runTest {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyTrackButtonIsShown()
        showDetailsRobot.clickTrackButton()
        showDetailsRobot.verifyStopTrackingButtonIsShown()
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
