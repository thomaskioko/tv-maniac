package com.thomaskioko.tvmaniac.app.test.compose.flows.sheet

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class EpisodeSheetFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val pilotSeasonNumber = 1L

    @Test
    fun givenAuthenticatedUser_whenUpNextCardClicked_thenOpensEpisodeSheet() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot
            .assertEpisodeSheetDisplayed()
    }

    @Test
    fun givenEpisodeSheet_whenOpened_thenShowsAllActionItems() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot
            .assertActionItemDisplayed(EpisodeSheetActionItem.TOGGLE_WATCHED)
            .assertActionItemDisplayed(EpisodeSheetActionItem.OPEN_SHOW)
            .assertActionItemDisplayed(EpisodeSheetActionItem.OPEN_SEASON)
            .assertActionItemDisplayed(EpisodeSheetActionItem.UNFOLLOW)
    }

    @Test
    fun givenEpisodeSheet_whenOpenShowClicked_thenNavigatesToShowDetails() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot
            .clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)

        showDetailsRobot
            .assertStopTrackingButtonDisplayed()
    }

    @Test
    fun givenEpisodeSheet_whenOpenSeasonClicked_thenNavigatesToSeasonDetails() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot
            .clickActionItem(EpisodeSheetActionItem.OPEN_SEASON)

        seasonDetailsRobot
            .assertSeasonDetailsDisplayed()
            .scrollToEpisodeRow(pilotEpisodeTraktId)
            .assertEpisodeRowDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenEpisodeSheet_whenToggleWatchedClicked_thenMarksEpisodeWatched() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot
            .clickActionItem(EpisodeSheetActionItem.TOGGLE_WATCHED)

        homeRobot.clickLibraryTab()
        libraryRobot.clickShowRow(breakingBadTraktId)
        showDetailsRobot
            .assertShowDetailsDisplayed()
            .clickSeasonChip(seasonNumber = pilotSeasonNumber)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenEpisodeSheet_whenUnfollowClicked_thenRemovesShowFromLibrary() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot
            .clickActionItem(EpisodeSheetActionItem.UNFOLLOW)

        homeRobot
            .clickLibraryTab()

        libraryRobot
            .assertShowRowDoesNotExist(breakingBadTraktId)
    }

    private fun AppFlowScope.openEpisodeSheetFromUpNextCard() {
        rootRobot.dismissNotificationRationale()

        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickLibraryTab()
            .assertTabSelected(HomeTestTags.LIBRARY_TAB)

        libraryRobot
            .scrollToShowRow(breakingBadTraktId)
            .assertShowRowDisplayed(breakingBadTraktId)

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertEpisodeRowDisplayed(breakingBadTraktId)

        homeRobot
            .clickDiscoverTab()
            .assertTabSelected(HomeTestTags.DISCOVER_TAB)

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickUpNextCard(breakingBadTraktId)

        episodeSheetRobot
            .assertEpisodeSheetDisplayed()
    }
}
