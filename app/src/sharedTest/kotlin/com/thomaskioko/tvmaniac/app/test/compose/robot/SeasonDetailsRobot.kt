package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags

internal class SeasonDetailsRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertSeasonDetailsDisplayed() {
        assertDisplayed(SeasonDetailsTestTags.SCREEN_TEST_TAG)
    }

    fun assertEpisodeRowDisplayed(episodeId: Long) {
        assertExists(SeasonDetailsTestTags.episodeRow(episodeId))
    }

    fun assertEpisodeRowDoesNotExist(episodeId: Long) {
        assertDoesNotExist(SeasonDetailsTestTags.episodeRow(episodeId))
    }

    fun clickEpisodeHeader() {
        click(SeasonDetailsTestTags.EPISODE_HEADER_TEST_TAG, useSemanticsAction = true)
    }

    fun clickEpisodeRow(episodeId: Long) {
        click(SeasonDetailsTestTags.episodeRow(episodeId), useSemanticsAction = true)
    }

    fun clickMarkWatched(episodeId: Long) {
        click(SeasonDetailsTestTags.markEpisodeWatchedButton(episodeId), useSemanticsAction = true)
    }

    fun clickMarkUnwatched(episodeId: Long) {
        click(SeasonDetailsTestTags.markEpisodeUnwatchedButton(episodeId), useSemanticsAction = true)
    }

    fun assertMarkUnwatchedDisplayed(episodeId: Long) {
        assertExists(SeasonDetailsTestTags.markEpisodeUnwatchedButton(episodeId))
    }

    fun assertMarkWatchedDisplayed(episodeId: Long) {
        assertExists(SeasonDetailsTestTags.markEpisodeWatchedButton(episodeId))
    }

    fun clickSeasonWatchedToggle() {
        click(SeasonDetailsTestTags.SEASON_WATCHED_TOGGLE_TEST_TAG, useSemanticsAction = true)
    }

    fun assertMarkPreviousEpisodesDialogDisplayed() {
        assertExists(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertMarkPreviousEpisodesDialogDoesNotExist() {
        assertDoesNotExist(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickMarkPreviousEpisodesConfirm() {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickMarkPreviousEpisodesDismiss() {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun assertUnwatchEpisodeDialogDisplayed() {
        assertExists(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertUnwatchEpisodeDialogDoesNotExist() {
        assertDoesNotExist(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickUnwatchEpisodeConfirm() {
        click(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickUnwatchEpisodeDismiss() {
        click(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun assertMarkPreviousSeasonsDialogDisplayed() {
        assertExists(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertMarkPreviousSeasonsDialogDoesNotExist() {
        assertDoesNotExist(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickMarkPreviousSeasonsConfirm() {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickMarkPreviousSeasonsDismiss() {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun assertUnwatchSeasonDialogDisplayed() {
        assertExists(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertUnwatchSeasonDialogDoesNotExist() {
        assertDoesNotExist(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickUnwatchSeasonConfirm() {
        click(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickUnwatchSeasonDismiss() {
        click(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }
}
