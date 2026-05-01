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
    fun givenAuthenticatedUser_whenUpNextCardClicked_thenOpensEpisodeSheet() {
        openEpisodeSheetFromUpNextCard()
        episodeSheetRobot.assertEpisodeSheetDisplayed()
    }

    @Test
    fun givenEpisodeSheet_whenOpened_thenShowsAllActionItems() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.assertActionItemDisplayed(EpisodeSheetActionItem.TOGGLE_WATCHED)
        episodeSheetRobot.assertActionItemDisplayed(EpisodeSheetActionItem.OPEN_SHOW)
        episodeSheetRobot.assertActionItemDisplayed(EpisodeSheetActionItem.OPEN_SEASON)
        episodeSheetRobot.assertActionItemDisplayed(EpisodeSheetActionItem.UNFOLLOW)
    }

    @Test
    fun givenEpisodeSheet_whenOpenShowClicked_thenNavigatesToShowDetails() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)

        showDetailsRobot.assertStopTrackingButtonDisplayed()
    }

    @Test
    fun givenEpisodeSheet_whenOpenSeasonClicked_thenNavigatesToSeasonDetails() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SEASON)

        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenEpisodeSheet_whenToggleWatchedClicked_thenMarksEpisodeWatched() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.TOGGLE_WATCHED)

        homeRobot.clickLibraryTab()
        libraryRobot.clickShowRow(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = pilotSeasonNumber)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenEpisodeSheet_whenUnfollowClicked_thenRemovesShowFromLibrary() {
        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.UNFOLLOW)

        homeRobot.clickLibraryTab()
        libraryRobot.assertShowRowDoesNotExist(breakingBadTraktId)
    }

    private fun openEpisodeSheetFromUpNextCard() {
        rootRobot.dismissNotificationRationale()

        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot.clickLibraryTab()
        libraryRobot.assertShowRowDisplayed(breakingBadTraktId)

        homeRobot.clickProgressTab()
        progressRobot.assertEpisodeRowDisplayed(breakingBadTraktId)

        homeRobot.clickDiscoverTab()
        discoverRobot.clickUpNextCard(breakingBadTraktId)

        episodeSheetRobot.assertEpisodeSheetDisplayed()
    }
}
