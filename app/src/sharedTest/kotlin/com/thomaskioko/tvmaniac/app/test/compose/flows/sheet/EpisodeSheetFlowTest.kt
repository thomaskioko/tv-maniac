package com.thomaskioko.tvmaniac.app.test.compose.flows.sheet

import com.thomaskioko.tvmaniac.accountmanager.api.SyncProviderSource
import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import com.thomaskioko.tvmaniac.util.testing.FlakyTests
import org.junit.Test

internal class EpisodeSheetFlowTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L
    private val pilotEpisodeTraktId = 73640L
    private val pilotSeasonNumber = 1L
    private val ratedStarValue = 8

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
            .assertActionItemDisplayed(EpisodeSheetActionItem.MARK_WATCHED)
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
    @FlakyTests(count = 2)
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
    fun givenEpisodeSheet_whenMarkWatchedClicked_thenMarksEpisodeWatched() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot
            .clickActionItem(EpisodeSheetActionItem.MARK_WATCHED)

        watchDateSelectionRobot
            .assertSheetDisplayed()
            .clickJustNow()

        homeRobot.clickMyShowsTab()
        watchlistRobot.clickShowCard(breakingBadTmdbId)
        showDetailsRobot
            .assertShowDetailsDisplayed()
            .clickSeasonChip(seasonNumber = pilotSeasonNumber)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
    }

    @Test
    @FlakyTests(count = 2)
    fun givenWatchedEpisode_whenMarkedWatchedAgain_thenSheetShowsPlayCount() = runAppFlowTest {
        scenarios.stubAuthenticatedSync()

        rootRobot.dismissNotificationRationale()
        discoverRobot.assertDiscoverScreenDisplayed()

        openSeasonDetails()

        seasonDetailsRobot.clickEpisodeRow(pilotEpisodeTraktId)
        episodeSheetRobot
            .assertEpisodeSheetDisplayed()
            .assertPlayCountDoesNotExist()
            .clickActionItem(EpisodeSheetActionItem.MARK_WATCHED)

        watchDateSelectionRobot
            .assertSheetDisplayed()
            .clickJustNow()

        seasonDetailsRobot
            .assertSeasonDetailsDisplayed()
            .clickEpisodeRow(pilotEpisodeTraktId)

        episodeSheetRobot
            .assertEpisodeSheetDisplayed()
            .assertPlayCountDisplayed(count = 2)
    }

    @Test
    @FlakyTests(count = 2)
    fun givenEpisodeSheet_whenEpisodeRated_thenRatingPersistsAndCanBeCleared() = runAppFlowTest {
        scenarios.stubTmdb()
        scenarios.stubActiveProvider(SyncProviderSource.TRAKT)

        openEpisodeSheetFromUpNextCard()

        episodeSheetRobot
            .clickRateAction()

        ratingSheetRobot
            .assertSheetDisplayed()
            .assertClearRatingButtonDoesNotExist()
            .clickStar(ratedStarValue)
            .assertClearRatingButtonDisplayed()
            .clickClearRatingButton()
            .assertClearRatingButtonDoesNotExist()
    }

    private fun AppFlowScope.openSeasonDetails() {
        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .scrollToShowCard(breakingBadTmdbId)
            .clickShowCard(breakingBadTmdbId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .clickSeasonChip(seasonNumber = pilotSeasonNumber)

        seasonDetailsRobot
            .assertSeasonDetailsDisplayed()
            .assertEpisodeRowDisplayed(pilotEpisodeTraktId)
    }

    private fun AppFlowScope.openEpisodeSheetFromUpNextCard() {
        rootRobot.dismissNotificationRationale()

        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot
            .clickMyShowsTab()
            .assertTabSelected(HomeTestTags.MY_SHOWS_TAB)

        watchlistRobot
            .scrollToShowCard(breakingBadTmdbId)
            .assertShowCardDisplayed(breakingBadTmdbId)

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertUpNextTabSelected()
            .assertUpNextPageDisplayed()
            .assertUpNextEpisodeDisplayed(breakingBadTmdbId)

        homeRobot
            .clickDiscoverTab()
            .assertTabSelected(HomeTestTags.DISCOVER_TAB)

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickUpNextCard(breakingBadTmdbId)

        episodeSheetRobot
            .assertEpisodeSheetDisplayed()
    }
}
