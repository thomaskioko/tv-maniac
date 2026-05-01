package com.thomaskioko.tvmaniac.app.test.compose.flows.upnext

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Before
import org.junit.Test

internal class UpNextFlowTests : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L

    @Before
    fun setUp() {
        scenarios.stubAuthenticatedSync()
    }

    @Test
    fun shouldShowEpisodeRowForFollowedShow() {
        navigateToUpNext()

        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
    }

    @Test
    fun shouldMarkEpisodeWatchedWhenWatchedButtonIsTapped() {
        navigateToUpNext()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)

        progressRobot.clickWatchedButton(breakingBadTraktId)

        progressRobot.clickEpisodeRow(breakingBadTraktId)
        seasonDetailsRobot.verifySeasonDetailsIsShown()
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun shouldAdvanceToNextEpisodeAndUpdateCountWhenWatchedButtonIsTapped() {
        navigateToUpNext()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
        progressRobot.verifyEpisodeMetaIsShown(breakingBadTraktId, "S01E01")
        progressRobot.verifyProgressCountIsShown(breakingBadTraktId, "0/62")

        scenarios.upNext.stubProgressAfterPilotWatched(breakingBadTraktId)
        progressRobot.clickWatchedButton(breakingBadTraktId)

        progressRobot.verifyEpisodeMetaIsShown(breakingBadTraktId, "S01E02")
        progressRobot.verifyProgressCountIsShown(breakingBadTraktId, "1/62")
    }

    @Test
    fun shouldNavigateToSeasonDetailsWhenEpisodeRowIsTapped() {
        navigateToUpNext()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)

        progressRobot.clickEpisodeRow(breakingBadTraktId)

        seasonDetailsRobot.verifySeasonDetailsIsShown()
    }

    private fun navigateToUpNext() {
        rootRobot.verifyNotificationRationaleIsShownAndDismissed()
        discoverRobot.verifyDiscoverScreenIsShown()

        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)

        homeRobot.clickProgressTab()
    }
}
