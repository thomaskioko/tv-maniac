package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags

@OptIn(ExperimentalTestApi::class)
internal class SeasonDetailsRobot(composeUi: ComposeUiTest) : BaseRobot<SeasonDetailsRobot>(composeUi) {

    fun assertSeasonDetailsDisplayed() = apply {
        assertDisplayed(SeasonDetailsTestTags.SCREEN_TEST_TAG)
    }

    fun assertEpisodeRowDisplayed(episodeId: Long) = apply {
        assertExists(SeasonDetailsTestTags.episodeRow(episodeId))
    }

    fun scrollToEpisodeRow(episodeId: Long) = apply {
        val tag = SeasonDetailsTestTags.episodeRow(episodeId)
        scrollToListTag(SeasonDetailsTestTags.SEASON_DETAILS_LIST_TEST_TAG, tag)
    }

    fun assertEpisodeRowDoesNotExist(episodeId: Long) = apply {
        assertDoesNotExist(SeasonDetailsTestTags.episodeRow(episodeId))
    }

    fun clickEpisodeHeader() = apply {
        scrollToListTag(SeasonDetailsTestTags.SEASON_DETAILS_LIST_TEST_TAG, SeasonDetailsTestTags.EPISODE_HEADER_TEST_TAG)
        click(SeasonDetailsTestTags.EPISODE_HEADER_TEST_TAG)
    }

    fun clickEpisodeRow(episodeId: Long) = apply {
        scrollToEpisodeRow(episodeId)
        click(SeasonDetailsTestTags.episodeRow(episodeId))
    }

    fun clickBackButton() = apply {
        click(SeasonDetailsTestTags.BACK_BUTTON_TEST_TAG, useUnmergedTree = true)
    }

    fun clickMarkWatched(episodeId: Long) = apply {
        scrollToMarkWatchedButton(episodeId)
        click(SeasonDetailsTestTags.markEpisodeWatchedButton(episodeId))
    }

    fun clickMarkUnwatched(episodeId: Long) = apply {
        scrollToMarkUnwatchedButton(episodeId)
        click(SeasonDetailsTestTags.markEpisodeUnwatchedButton(episodeId))
    }

    fun assertMarkUnwatchedDisplayed(episodeId: Long) = apply {
        assertExists(SeasonDetailsTestTags.markEpisodeUnwatchedButton(episodeId))
    }

    fun scrollToMarkUnwatchedButton(episodeId: Long) = apply {
        val tag = SeasonDetailsTestTags.markEpisodeUnwatchedButton(episodeId)
        scrollToListTag(SeasonDetailsTestTags.SEASON_DETAILS_LIST_TEST_TAG, tag)
    }

    fun assertMarkWatchedDisplayed(episodeId: Long) = apply {
        assertExists(SeasonDetailsTestTags.markEpisodeWatchedButton(episodeId))
    }

    fun scrollToMarkWatchedButton(episodeId: Long) = apply {
        val tag = SeasonDetailsTestTags.markEpisodeWatchedButton(episodeId)
        scrollToListTag(SeasonDetailsTestTags.SEASON_DETAILS_LIST_TEST_TAG, tag)
    }

    fun clickSeasonWatchedToggle() = apply {
        scrollToListTag(SeasonDetailsTestTags.SEASON_DETAILS_LIST_TEST_TAG, SeasonDetailsTestTags.SEASON_WATCHED_TOGGLE_TEST_TAG)
        click(SeasonDetailsTestTags.SEASON_WATCHED_TOGGLE_TEST_TAG)
    }

    fun assertMarkPreviousEpisodesDialogDisplayed() = apply {
        assertExists(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertMarkPreviousEpisodesDialogDoesNotExist() = apply {
        assertDoesNotExist(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickMarkPreviousEpisodesConfirm() = apply {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickMarkPreviousEpisodesDismiss() = apply {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_DISMISS_BUTTON_TEST_TAG)
    }

    fun assertUnwatchEpisodeDialogDisplayed() = apply {
        assertExists(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertUnwatchEpisodeDialogDoesNotExist() = apply {
        assertDoesNotExist(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickUnwatchEpisodeConfirm() = apply {
        click(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickUnwatchEpisodeDismiss() = apply {
        click(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_DISMISS_BUTTON_TEST_TAG)
    }

    fun assertMarkPreviousSeasonsDialogDisplayed() = apply {
        assertExists(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertMarkPreviousSeasonsDialogDoesNotExist() = apply {
        assertDoesNotExist(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickMarkPreviousSeasonsConfirm() = apply {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickMarkPreviousSeasonsDismiss() = apply {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_DISMISS_BUTTON_TEST_TAG)
    }

    fun assertUnwatchSeasonDialogDisplayed() = apply {
        assertExists(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertUnwatchSeasonDialogDoesNotExist() = apply {
        assertDoesNotExist(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickUnwatchSeasonConfirm() = apply {
        click(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickUnwatchSeasonDismiss() = apply {
        click(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_DISMISS_BUTTON_TEST_TAG)
    }
}
