package com.thomaskioko.tvmaniac.app.test.compose.flows.seasons

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import org.junit.Before
import org.junit.Test

internal class SeasonFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val thirdEpisodeTraktId = 73484L
    private val seasonTwoFirstEpisodeTraktId = 73489L

    @Before
    fun stubEndpoints() {
        scenarios.discover.stubBrowseGraph()
    }

    @Test
    fun shouldToggleEpisodeWatchedStateWhenWatchedButtonIsTapped() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(pilotEpisodeTraktId)
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(pilotEpisodeTraktId)

        rootRobot.pressBack()
        showDetailsRobot.verifyContinueTrackingSectionIsShown()
        showDetailsRobot.verifyContinueTrackingEpisodeIsShown(secondEpisodeTraktId)
    }

    @Test
    fun shouldShowMarkPreviousEpisodesDialogWhenMarkingEpisodeWithUnwatchedPredecessors() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifyEpisodeRowIsShown(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.verifyMarkPreviousEpisodesDialogIsShown()
        seasonDetailsRobot.clickMarkPreviousEpisodesDismiss()
        seasonDetailsRobot.verifyMarkPreviousEpisodesDialogIsHidden()

        rootRobot.pressBack()
        showDetailsRobot.verifyContinueTrackingSectionIsShown()
        showDetailsRobot.verifyContinueTrackingEpisodeIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun shouldMarkAllPreviousEpisodesWhenMarkAllConfirmed() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifyEpisodeRowIsShown(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkPreviousEpisodesConfirm()
        seasonDetailsRobot.verifyMarkPreviousEpisodesDialogIsHidden()
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(pilotEpisodeTraktId)
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(secondEpisodeTraktId)

        rootRobot.pressBack()
        showDetailsRobot.verifyContinueTrackingSectionIsShown()
        showDetailsRobot.verifyContinueTrackingEpisodeIsShown(thirdEpisodeTraktId)
    }

    @Test
    fun shouldMarkOnlyCurrentEpisodeWhenJustThisIsTapped() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifyEpisodeRowIsShown(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.clickMarkPreviousEpisodesDismiss()
        seasonDetailsRobot.verifyMarkPreviousEpisodesDialogIsHidden()
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(secondEpisodeTraktId)
        seasonDetailsRobot.verifyMarkWatchedIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun shouldShowUnwatchEpisodeDialogWhenWatchedEpisodeToggled() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(pilotEpisodeTraktId)
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkUnwatched(pilotEpisodeTraktId)
        seasonDetailsRobot.verifyUnwatchEpisodeDialogIsShown()
        seasonDetailsRobot.clickUnwatchEpisodeDismiss()
        seasonDetailsRobot.verifyUnwatchEpisodeDialogIsHidden()

        rootRobot.pressBack()
        showDetailsRobot.verifyContinueTrackingSectionIsShown()
        showDetailsRobot.verifyContinueTrackingEpisodeIsShown(secondEpisodeTraktId)
    }

    @Test
    fun shouldUnwatchEpisodeWhenUnwatchDialogConfirmed() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        trackShow()
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkUnwatched(pilotEpisodeTraktId)
        seasonDetailsRobot.clickUnwatchEpisodeConfirm()
        seasonDetailsRobot.verifyUnwatchEpisodeDialogIsHidden()
        seasonDetailsRobot.verifyMarkWatchedIsShown(pilotEpisodeTraktId)

        rootRobot.pressBack()
        showDetailsRobot.verifyContinueTrackingSectionIsShown()
        showDetailsRobot.verifyContinueTrackingEpisodeIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun shouldMarkAllPreviousSeasonsWatchedWhenConfirmed() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 2L)
        seasonDetailsRobot.verifyEpisodeRowIsShown(seasonTwoFirstEpisodeTraktId)
        seasonDetailsRobot.clickSeasonWatchedToggle()
        seasonDetailsRobot.verifyMarkPreviousSeasonsDialogIsShown()
        seasonDetailsRobot.clickMarkPreviousSeasonsConfirm()
        seasonDetailsRobot.verifyMarkPreviousSeasonsDialogIsHidden()
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(seasonTwoFirstEpisodeTraktId)

        rootRobot.pressBack()
        showDetailsRobot.clickSeasonChip(seasonNumber = 2L)

        seasonDetailsRobot.clickSeasonWatchedToggle()
        seasonDetailsRobot.verifyUnwatchSeasonDialogIsShown()
        seasonDetailsRobot.clickUnwatchSeasonConfirm()
        seasonDetailsRobot.verifyUnwatchSeasonDialogIsHidden()
        seasonDetailsRobot.verifyMarkWatchedIsShown(seasonTwoFirstEpisodeTraktId)
    }

    private fun trackShow() {
        showDetailsRobot.verifyTrackButtonIsShown()
        showDetailsRobot.clickTrackButton()
        rootRobot.verifyNotificationRationaleIsShownAndDismissed()
        showDetailsRobot.verifyStopTrackingButtonIsShown()
    }
}
