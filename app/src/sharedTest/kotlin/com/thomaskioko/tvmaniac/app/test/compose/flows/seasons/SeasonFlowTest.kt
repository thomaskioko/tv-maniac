package com.thomaskioko.tvmaniac.app.test.compose.flows.seasons

import com.thomaskioko.tvmaniac.app.test.AppFlowScope
import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Test

internal class SeasonFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val thirdEpisodeTraktId = 73484L
    private val seasonTwoFirstEpisodeTraktId = 73489L

    @Test
    fun seasonUserJourney() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()

        // 1. Mark episode as watched & check continue tracking
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(pilotEpisodeTraktId)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)

        seasonDetailsRobot.clickBackButton()
        showDetailsRobot.assertContinueTrackingSectionDisplayed()
        showDetailsRobot.assertContinueTrackingEpisodeDisplayed(secondEpisodeTraktId)

        // 2. Unwatch episode - Dismiss & Confirm
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.clickMarkUnwatched(pilotEpisodeTraktId)
        seasonDetailsRobot.assertUnwatchEpisodeDialogDisplayed()
        seasonDetailsRobot.clickUnwatchEpisodeDismiss()
        seasonDetailsRobot.assertUnwatchEpisodeDialogDoesNotExist()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)

        seasonDetailsRobot.clickMarkUnwatched(pilotEpisodeTraktId)
        seasonDetailsRobot.clickUnwatchEpisodeConfirm()
        seasonDetailsRobot.assertUnwatchEpisodeDialogDoesNotExist()
        seasonDetailsRobot.assertMarkWatchedDisplayed(pilotEpisodeTraktId)

        // 3. Mark episode with predecessors - Dismiss (Just this)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.assertMarkPreviousEpisodesDialogDisplayed()
        seasonDetailsRobot.clickMarkPreviousEpisodesDismiss()
        seasonDetailsRobot.assertMarkPreviousEpisodesDialogDoesNotExist()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(secondEpisodeTraktId)
        seasonDetailsRobot.assertMarkWatchedDisplayed(pilotEpisodeTraktId)

        // 4. Mark with predecessors - Confirm (Mark All Previous)
        // Reset state for second episode first
        seasonDetailsRobot.clickMarkUnwatched(secondEpisodeTraktId)
        seasonDetailsRobot.clickUnwatchEpisodeConfirm()

        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkPreviousEpisodesConfirm()
        seasonDetailsRobot.assertMarkPreviousEpisodesDialogDoesNotExist()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(secondEpisodeTraktId)

        // 5. Verify Continue Tracking
        seasonDetailsRobot.clickBackButton()
        showDetailsRobot.assertContinueTrackingSectionDisplayed()
        showDetailsRobot.assertContinueTrackingEpisodeDisplayed(thirdEpisodeTraktId)

        // 6. Multi-Season flow - Mark Previous Seasons
        showDetailsRobot.clickSeasonChip(seasonNumber = 2L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(seasonTwoFirstEpisodeTraktId)
        seasonDetailsRobot.clickSeasonWatchedToggle()
        seasonDetailsRobot.assertMarkPreviousSeasonsDialogDisplayed()
        seasonDetailsRobot.clickMarkPreviousSeasonsConfirm()
        seasonDetailsRobot.assertMarkPreviousSeasonsDialogDoesNotExist()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(seasonTwoFirstEpisodeTraktId)

        // 7. Unwatch Season
        seasonDetailsRobot.clickBackButton()
        showDetailsRobot.clickSeasonChip(seasonNumber = 2L)
        seasonDetailsRobot.clickSeasonWatchedToggle()
        seasonDetailsRobot.assertUnwatchSeasonDialogDisplayed()
        seasonDetailsRobot.clickUnwatchSeasonConfirm()
        seasonDetailsRobot.assertUnwatchSeasonDialogDoesNotExist()
        seasonDetailsRobot.assertMarkWatchedDisplayed(seasonTwoFirstEpisodeTraktId)
    }
}
