package com.thomaskioko.tvmaniac.app.test.compose.flows.upnext

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class UpNextFlowTests : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L

    @Test
    fun upNextUserJourney() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        navigateToUpNext()

        // 1. Verify Episode Row & Meta
        progressRobot
            .assertEpisodeRowDisplayed(breakingBadTraktId)
            .assertEpisodeMetaDisplayed(breakingBadTraktId, "S01E01")
            .assertProgressCountDisplayed(breakingBadTraktId, "0/62")
            .clickEpisodeRow(breakingBadTraktId)
            .assertSeasonDetailsDisplayed()
            .clickBackButton()

        // 3. Mark Watched & Verify advancement
        scenarios.upNext.stubProgressAfterPilotWatched(breakingBadTraktId)

        progressRobot
            .clickWatchedButton(breakingBadTraktId)
            .assertEpisodeMetaDisplayed(breakingBadTraktId, "S01E02")
            .assertProgressCountDisplayed(breakingBadTraktId, "1/62")
            // 4. Verify in Season Details
            .clickEpisodeRow(breakingBadTraktId)
            .assertSeasonDetailsDisplayed()
            .assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
    }

    private fun AppFlowScope.navigateToUpNext() {
        rootRobot
            .dismissNotificationRationale()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickLibraryTab()
            .assertTabSelected(HomeTestTags.LIBRARY_TAB)

        libraryRobot
            .scrollToShowRow(breakingBadTraktId)
            .assertShowRowDisplayed(breakingBadTraktId)

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)
    }
}
