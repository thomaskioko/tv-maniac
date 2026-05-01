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
    fun givenSeasonDetails_whenWatchedButtonClicked_thenTogglesWatchedState() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(pilotEpisodeTraktId)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)

        rootRobot.pressBack()
        showDetailsRobot.assertContinueTrackingSectionDisplayed()
        showDetailsRobot.assertContinueTrackingEpisodeDisplayed(secondEpisodeTraktId)
    }

    @Test
    fun givenSeasonDetails_whenMarkingEpisodeWithPredecessors_thenShowsDialog() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.assertMarkPreviousEpisodesDialogDisplayed()
        seasonDetailsRobot.clickMarkPreviousEpisodesDismiss()
        seasonDetailsRobot.assertMarkPreviousEpisodesDialogDoesNotExist()

        rootRobot.pressBack()
        showDetailsRobot.assertContinueTrackingSectionDisplayed()
        showDetailsRobot.assertContinueTrackingEpisodeDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenSeasonDetails_whenMarkAllConfirmed_thenMarksAllPreviousEpisodes() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkPreviousEpisodesConfirm()
        seasonDetailsRobot.assertMarkPreviousEpisodesDialogDoesNotExist()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(secondEpisodeTraktId)

        rootRobot.pressBack()
        showDetailsRobot.assertContinueTrackingSectionDisplayed()
        showDetailsRobot.assertContinueTrackingEpisodeDisplayed(thirdEpisodeTraktId)
    }

    @Test
    fun givenSeasonDetails_whenJustThisClicked_thenMarksOnlyCurrentEpisode() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkPreviousEpisodesDismiss()
        seasonDetailsRobot.assertMarkPreviousEpisodesDialogDoesNotExist()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(secondEpisodeTraktId)
        seasonDetailsRobot.assertMarkWatchedDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenSeasonDetails_whenWatchedEpisodeToggled_thenShowsUnwatchDialog() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(pilotEpisodeTraktId)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkUnwatched(pilotEpisodeTraktId)
        seasonDetailsRobot.assertUnwatchEpisodeDialogDisplayed()
        seasonDetailsRobot.clickUnwatchEpisodeDismiss()
        seasonDetailsRobot.assertUnwatchEpisodeDialogDoesNotExist()

        rootRobot.pressBack()
        showDetailsRobot.assertContinueTrackingSectionDisplayed()
        showDetailsRobot.assertContinueTrackingEpisodeDisplayed(secondEpisodeTraktId)
    }

    @Test
    fun givenSeasonDetails_whenUnwatchConfirmed_thenUnwatchesEpisode() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(pilotEpisodeTraktId)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkUnwatched(pilotEpisodeTraktId)
        seasonDetailsRobot.clickUnwatchEpisodeConfirm()
        seasonDetailsRobot.assertUnwatchEpisodeDialogDoesNotExist()
        seasonDetailsRobot.assertMarkWatchedDisplayed(pilotEpisodeTraktId)

        rootRobot.pressBack()
        showDetailsRobot.assertContinueTrackingSectionDisplayed()
        showDetailsRobot.assertContinueTrackingEpisodeDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenSeasonDetails_whenMarkPreviousSeasonsConfirmed_thenMarksAllPreviousSeasons() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 2L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(seasonTwoFirstEpisodeTraktId)
        seasonDetailsRobot.clickSeasonWatchedToggle()
        seasonDetailsRobot.assertMarkPreviousSeasonsDialogDisplayed()
        seasonDetailsRobot.clickMarkPreviousSeasonsConfirm()
        seasonDetailsRobot.assertMarkPreviousSeasonsDialogDoesNotExist()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(seasonTwoFirstEpisodeTraktId)

        rootRobot.pressBack()
        showDetailsRobot.clickSeasonChip(seasonNumber = 2L)

        seasonDetailsRobot.clickSeasonWatchedToggle()
        seasonDetailsRobot.assertUnwatchSeasonDialogDisplayed()
        seasonDetailsRobot.clickUnwatchSeasonConfirm()
        seasonDetailsRobot.assertUnwatchSeasonDialogDoesNotExist()
        seasonDetailsRobot.assertMarkWatchedDisplayed(seasonTwoFirstEpisodeTraktId)
    }

    private fun AppFlowScope.trackShow() {
        showDetailsRobot.assertTrackButtonDisplayed()
        showDetailsRobot.clickTrackButton()
        rootRobot.dismissNotificationRationale()
        showDetailsRobot.assertStopTrackingButtonDisplayed()
    }
}
