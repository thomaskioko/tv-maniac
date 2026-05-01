package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags

internal class SeasonDetailsRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifySeasonDetailsIsShown() {
        verifyTagShown(SeasonDetailsTestTags.SCREEN_TEST_TAG)
    }

    fun verifyEpisodeRowIsShown(episodeId: Long) {
        verifyTagExists(SeasonDetailsTestTags.episodeRow(episodeId))
    }

    fun verifyEpisodeRowIsHidden(episodeId: Long) {
        verifyTagHidden(SeasonDetailsTestTags.episodeRow(episodeId))
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

    fun verifyMarkUnwatchedIsShown(episodeId: Long) {
        verifyTagExists(SeasonDetailsTestTags.markEpisodeUnwatchedButton(episodeId))
    }

    fun verifyMarkWatchedIsShown(episodeId: Long) {
        verifyTagExists(SeasonDetailsTestTags.markEpisodeWatchedButton(episodeId))
    }

    fun clickSeasonWatchedToggle() {
        click(SeasonDetailsTestTags.SEASON_WATCHED_TOGGLE_TEST_TAG, useSemanticsAction = true)
    }

    fun verifyMarkPreviousEpisodesDialogIsShown() {
        verifyTagExists(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun verifyMarkPreviousEpisodesDialogIsHidden() {
        verifyTagHidden(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickMarkPreviousEpisodesConfirm() {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickMarkPreviousEpisodesDismiss() {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_EPISODES_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun verifyUnwatchEpisodeDialogIsShown() {
        verifyTagExists(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun verifyUnwatchEpisodeDialogIsHidden() {
        verifyTagHidden(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickUnwatchEpisodeConfirm() {
        click(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickUnwatchEpisodeDismiss() {
        click(SeasonDetailsTestTags.UNWATCH_EPISODE_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun verifyMarkPreviousSeasonsDialogIsShown() {
        verifyTagExists(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun verifyMarkPreviousSeasonsDialogIsHidden() {
        verifyTagHidden(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickMarkPreviousSeasonsConfirm() {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickMarkPreviousSeasonsDismiss() {
        click(SeasonDetailsTestTags.MARK_PREVIOUS_SEASONS_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun verifyUnwatchSeasonDialogIsShown() {
        verifyTagExists(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun verifyUnwatchSeasonDialogIsHidden() {
        verifyTagHidden(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickUnwatchSeasonConfirm() {
        click(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_CONFIRM_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickUnwatchSeasonDismiss() {
        click(SeasonDetailsTestTags.UNWATCH_SEASON_DIALOG_DISMISS_BUTTON_TEST_TAG, useSemanticsAction = true)
    }
}
