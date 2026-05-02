package com.thomaskioko.tvmaniac.app.test.compose.flows.upnext

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class UpNextFlowTests : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L

    @Test
    fun upNextUserJourney() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        navigateToUpNext()

        // 1. Verify Episode Row & Meta
        progressRobot.assertEpisodeRowDisplayed(breakingBadTraktId)
        progressRobot.assertEpisodeMetaDisplayed(breakingBadTraktId, "S01E01")
        progressRobot.assertProgressCountDisplayed(breakingBadTraktId, "0/62")

        // 2. Click Episode Row -> Season Details
        progressRobot.clickEpisodeRow(breakingBadTraktId)
        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.clickBackButton()

        // 3. Mark Watched & Verify advancement
        scenarios.upNext.stubProgressAfterPilotWatched(breakingBadTraktId)
        progressRobot.clickWatchedButton(breakingBadTraktId)

        progressRobot.assertEpisodeMetaDisplayed(breakingBadTraktId, "S01E02")
        progressRobot.assertProgressCountDisplayed(breakingBadTraktId, "1/62")

        // 4. Verify in Season Details
        progressRobot.clickEpisodeRow(breakingBadTraktId)
        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
    }

    private fun AppFlowScope.navigateToUpNext() {
        rootRobot.dismissNotificationRationale()
        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot.clickLibraryTab()
        libraryRobot.assertShowRowDisplayed(breakingBadTraktId)

        homeRobot.clickProgressTab()
    }
}
