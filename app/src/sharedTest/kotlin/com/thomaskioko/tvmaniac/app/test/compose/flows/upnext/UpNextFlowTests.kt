package com.thomaskioko.tvmaniac.app.test.compose.flows.upnext

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class UpNextFlowTests : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L

    @Test
    fun givenAuthenticatedUser_whenShowFollowed_thenShowsEpisodeRow() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        navigateToUpNext()

        progressRobot.assertEpisodeRowDisplayed(breakingBadTraktId)
    }

    @Test
    fun givenUpNext_whenWatchedButtonClicked_thenMarksEpisodeWatched() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        navigateToUpNext()
        progressRobot.assertEpisodeRowDisplayed(breakingBadTraktId)

        progressRobot.clickWatchedButton(breakingBadTraktId)

        progressRobot.clickEpisodeRow(breakingBadTraktId)
        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenUpNext_whenWatchedButtonClicked_thenAdvancesToNextEpisode() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        navigateToUpNext()
        progressRobot.assertEpisodeRowDisplayed(breakingBadTraktId)
        progressRobot.assertEpisodeMetaDisplayed(breakingBadTraktId, "S01E01")
        progressRobot.assertProgressCountDisplayed(breakingBadTraktId, "0/62")

        scenarios.upNext.stubProgressAfterPilotWatched(breakingBadTraktId)
        progressRobot.clickWatchedButton(breakingBadTraktId)

        progressRobot.assertEpisodeMetaDisplayed(breakingBadTraktId, "S01E02")
        progressRobot.assertProgressCountDisplayed(breakingBadTraktId, "1/62")
    }

    @Test
    fun givenUpNext_whenEpisodeRowClicked_thenNavigatesToSeasonDetails() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        navigateToUpNext()
        progressRobot.assertEpisodeRowDisplayed(breakingBadTraktId)

        progressRobot.clickEpisodeRow(breakingBadTraktId)

        seasonDetailsRobot.assertSeasonDetailsDisplayed()
    }

    private fun AppFlowScope.navigateToUpNext() {
        rootRobot.dismissNotificationRationale()
        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot.clickLibraryTab()
        libraryRobot.assertShowRowDisplayed(breakingBadTraktId)

        homeRobot.clickProgressTab()
    }
}
