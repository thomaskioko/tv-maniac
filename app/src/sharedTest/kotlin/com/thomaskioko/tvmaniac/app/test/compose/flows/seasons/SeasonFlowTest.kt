package com.thomaskioko.tvmaniac.app.test.compose.flows.seasons

import com.thomaskioko.tvmaniac.accountmanager.api.AccountProvider
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class SeasonFlowTest : BaseAppFlowTest() {

    private val breakingBadTmdbId = 1396L
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val thirdEpisodeTraktId = 73484L
    private val seasonTwoFirstEpisodeTraktId = 73489L
    private val ratedStarValue = 8

    @Test
    fun seasonUserJourney() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)
        trackShow()

        // 1. Mark episode as watched & check continue tracking
        showDetailsRobot
            .clickSeasonChip(seasonNumber = 1L)
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)
            .scrollToEpisodeRow(pilotEpisodeTraktId)
            .assertEpisodeRowDisplayed(pilotEpisodeTraktId)
            .clickMarkWatched(pilotEpisodeTraktId)
            .assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
            .clickBackButton()

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertContinueTrackingSectionDisplayed()
            .assertContinueTrackingEpisodeDisplayed(secondEpisodeTraktId)

        // 2. Unwatch episode - Dismiss & Confirm
        showDetailsRobot
            .clickSeasonChip(seasonNumber = 1L)
            .clickMarkUnwatched(pilotEpisodeTraktId)
            .assertUnwatchEpisodeDialogDisplayed()
            .clickUnwatchEpisodeDismiss()
            .assertUnwatchEpisodeDialogDoesNotExist()
            .assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
            .clickMarkUnwatched(pilotEpisodeTraktId)
            .clickUnwatchEpisodeConfirm()
            .assertUnwatchEpisodeDialogDoesNotExist()
            .assertMarkWatchedDisplayed(pilotEpisodeTraktId)

        // 3. Mark episode with predecessors - Dismiss (Just this)
        seasonDetailsRobot
            .clickMarkWatched(secondEpisodeTraktId)
            .assertMarkPreviousEpisodesDialogDisplayed()
            .clickMarkPreviousEpisodesDismiss()
            .assertMarkPreviousEpisodesDialogDoesNotExist()
            .assertMarkUnwatchedDisplayed(secondEpisodeTraktId)
            .assertMarkWatchedDisplayed(pilotEpisodeTraktId)

        // 4. Mark with predecessors - Confirm (Mark All Previous)
        // Reset state for second episode first
        seasonDetailsRobot
            .clickMarkUnwatched(secondEpisodeTraktId)
            .clickUnwatchEpisodeConfirm()
            .clickMarkWatched(secondEpisodeTraktId)
            .clickMarkPreviousEpisodesConfirm()
            .assertMarkPreviousEpisodesDialogDoesNotExist()
            .assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
            .assertMarkUnwatchedDisplayed(secondEpisodeTraktId)
            .clickBackButton()

        // 5. Verify Continue Tracking
        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertContinueTrackingSectionDisplayed()
            .assertContinueTrackingEpisodeDisplayed(thirdEpisodeTraktId)
            .clickSeasonChip(seasonNumber = 2L)

        // 6. Multi-Season flow - Mark Previous Seasons
        seasonDetailsRobot
            .scrollToEpisodeRow(seasonTwoFirstEpisodeTraktId)
            .assertEpisodeRowDisplayed(seasonTwoFirstEpisodeTraktId)
            .clickSeasonWatchedToggle()
            .assertMarkPreviousSeasonsDialogDisplayed()
            .clickMarkPreviousSeasonsConfirm()
            .assertMarkPreviousSeasonsDialogDoesNotExist()
            .assertMarkUnwatchedDisplayed(seasonTwoFirstEpisodeTraktId)
            .clickBackButton()

        // 7. Continue Tracking stays visible with every episode watched
        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertContinueTrackingSectionDisplayed()
            .assertContinueTrackingEpisodeDisplayed(seasonTwoFirstEpisodeTraktId)

        // 8. Unwatch Season
        showDetailsRobot
            .clickSeasonChip(seasonNumber = 2L)

        seasonDetailsRobot
            .clickSeasonWatchedToggle()
            .assertUnwatchSeasonDialogDisplayed()
            .clickUnwatchSeasonConfirm()
            .assertUnwatchSeasonDialogDoesNotExist()
            .assertMarkWatchedDisplayed(seasonTwoFirstEpisodeTraktId)
    }

    @Test
    fun givenNoUnwatchedPreviousSeasons_whenSeasonToggledWatched_thenConfirmationDialogGatesTheWrite() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)
        trackShow()

        showDetailsRobot
            .clickSeasonChip(seasonNumber = 1L)
            .assertSeasonDetailsDisplayed()
            .assertDoesNotExist(HomeTestTags.NAVIGATION_BAR)

        seasonDetailsRobot
            .clickSeasonWatchedToggle()
            .assertWatchSeasonDialogDisplayed()
            .clickWatchSeasonDismiss()
            .assertWatchSeasonDialogDoesNotExist()
            .scrollToMarkWatchedButton(secondEpisodeTraktId)
            .assertMarkWatchedDisplayed(secondEpisodeTraktId)

        seasonDetailsRobot
            .clickSeasonWatchedToggle()
            .assertWatchSeasonDialogDisplayed()
            .clickWatchSeasonConfirm()
            .assertWatchSeasonDialogDoesNotExist()
            .scrollToMarkUnwatchedButton(secondEpisodeTraktId)
            .assertMarkUnwatchedDisplayed(secondEpisodeTraktId)
    }

    @Test
    fun givenAuthenticatedUser_whenSeasonRated_thenRatingPersistsAndCanBeCleared() = runAppFlowTest {
        scenarios.stubTmdb()
        scenarios.stubActiveProvider(AccountProvider.TRAKT)

        rootRobot.dismissNotificationRationale()

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .clickShowCard(breakingBadTmdbId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .clickSeasonChip(seasonNumber = 1L)
            .assertSeasonDetailsDisplayed()
            .clickRateButton()

        ratingSheetRobot
            .assertSheetDisplayed()
            .assertClearRatingButtonDoesNotExist()
            .clickStar(ratedStarValue)
            .assertClearRatingButtonDisplayed()
            .clickClearRatingButton()
            .assertClearRatingButtonDoesNotExist()
    }
}
