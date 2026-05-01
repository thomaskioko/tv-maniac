package com.thomaskioko.tvmaniac.app.test.compose.flows.sheet

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import org.junit.Before
import org.junit.Test

internal class EpisodeSheetFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val pilotSeasonNumber = 1L

    @Before
    fun setUp() {
        scenarios.stubAuthenticatedSync()
    }

    @Test
    fun shouldOpenEpisodeSheetFromDiscoverUpNextCard() {
        openEpisodeSheetFromUpNextCard()
        episodeSheetRobot.verifyEpisodeSheetIsShown()
    }

    @Test
    fun shouldShowAllSheetActionsWhenOpened() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.verifyActionItemIsShown(EpisodeSheetActionItem.TOGGLE_WATCHED)
        episodeSheetRobot.verifyActionItemIsShown(EpisodeSheetActionItem.OPEN_SHOW)
        episodeSheetRobot.verifyActionItemIsShown(EpisodeSheetActionItem.OPEN_SEASON)
        episodeSheetRobot.verifyActionItemIsShown(EpisodeSheetActionItem.UNFOLLOW)
    }

    @Test
    fun shouldOpenShowDetailsWhenSheetOpenShowActionIsTapped() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)

        showDetailsRobot.verifyStopTrackingButtonIsShown()
    }

    @Test
    fun shouldOpenSeasonDetailsWhenSheetOpenSeasonActionIsTapped() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SEASON)

        seasonDetailsRobot.verifySeasonDetailsIsShown()
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun shouldMarkEpisodeWatchedWhenToggleWatchedActionIsTapped() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.TOGGLE_WATCHED)

        homeRobot.clickLibraryTab()
        libraryRobot.clickShowRow(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = pilotSeasonNumber)
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun shouldUnfollowShowWhenUnfollowActionIsTapped() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.UNFOLLOW)

        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsHidden(breakingBadTraktId)
    }

    private fun openEpisodeSheetFromUpNextCard() {
        rootRobot.verifyNotificationRationaleIsShownAndDismissed()

        discoverRobot.verifyDiscoverScreenIsShown()

        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)

        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)

        homeRobot.clickDiscoverTab()
        discoverRobot.clickUpNextCard(breakingBadTraktId)

        episodeSheetRobot.verifyEpisodeSheetIsShown()
    }
}
