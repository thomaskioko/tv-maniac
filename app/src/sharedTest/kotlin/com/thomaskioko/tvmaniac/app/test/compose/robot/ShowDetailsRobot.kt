package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags

internal class ShowDetailsRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertShowDetailsDisplayed() {
        assertDisplayed(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun assertTrackButtonDisplayed() {
        assertDisplayed(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun assertStopTrackingButtonDisplayed() {
        assertDisplayed(ShowDetailsTestTags.STOP_TRACKING_BUTTON_TEST_TAG)
    }

    fun assertSeasonChipDisplayed(seasonNumber: Long) {
        assertExists(ShowDetailsTestTags.seasonChip(seasonNumber))
    }

    fun clickTrackButton() {
        click(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun clickContinueTrackingMarkWatched(episodeId: Long) {
        click(ShowDetailsTestTags.continueTrackingMarkWatchedButton(episodeId))
    }

    fun assertContinueTrackingEpisodeDisplayed(episodeId: Long) {
        assertExists(ShowDetailsTestTags.continueTrackingMarkWatchedButton(episodeId))
    }

    fun clickAddToListButton() {
        click(ShowDetailsTestTags.ADD_TO_LIST_BUTTON_TEST_TAG)
    }

    fun assertLoginPromptDisplayed() {
        assertExists(ShowDetailsTestTags.LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertLoginPromptDoesNotExist() {
        assertDoesNotExist(ShowDetailsTestTags.LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun confirmLoginPrompt() {
        click(ShowDetailsTestTags.LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickSeasonChip(seasonNumber: Long): SeasonDetailsRobot {
        click(ShowDetailsTestTags.seasonChip(seasonNumber), useSemanticsAction = true)
        return SeasonDetailsRobot(composeTestRule)
    }

    fun assertErrorStateDisplayed() {
        assertDisplayed(ShowDetailsTestTags.ERROR_RETRY_BUTTON_TEST_TAG)
    }

    fun clickRetryButton() {
        click(ShowDetailsTestTags.ERROR_RETRY_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickRefreshButton() {
        assertDisplayed(ShowDetailsTestTags.REFRESH_BUTTON_TEST_TAG)
        click(ShowDetailsTestTags.REFRESH_BUTTON_TEST_TAG)
    }

    fun assertCastListDisplayed() {
        assertDisplayed(ShowDetailsTestTags.CAST_LIST_TEST_TAG)
    }

    fun assertTrailersListDisplayed() {
        assertDisplayed(ShowDetailsTestTags.TRAILERS_LIST_TEST_TAG)
    }

    fun assertSimilarShowsListDisplayed() {
        assertDisplayed(ShowDetailsTestTags.SIMILAR_SHOWS_LIST_TEST_TAG)
    }

    fun assertContinueTrackingSectionDisplayed() {
        assertDisplayed(ShowDetailsTestTags.CONTINUE_TRACKING_SECTION_TEST_TAG)
    }

    fun assertListSheetDisplayed() {
        assertDisplayed(ShowDetailsTestTags.LIST_SHEET_TEST_TAG)
    }

    fun assertListSheetDoesNotExist() {
        composeTestRule.onNodeWithTag(
            ShowDetailsTestTags.LIST_SHEET_TEST_TAG,
            useUnmergedTree = true,
        ).assertIsNotDisplayed()
    }

    fun assertTraktListItemDisplayed(listId: Long) {
        assertExists(ShowDetailsTestTags.traktListItem(listId))
    }

    fun clickCloseListSheetButton() {
        click(ShowDetailsTestTags.LIST_SHEET_CLOSE_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickCreateListButton() {
        click(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun assertCreateListFieldDisplayed() {
        assertExists(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG)
    }

    fun assertCreateListFieldDoesNotExist() {
        assertDoesNotExist(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG)
    }

    fun typeCreateListName(name: String) {
        composeTestRule.onNodeWithTag(
            ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG,
            useUnmergedTree = true,
        ).performTextInput(name)
    }

    fun clickCreateListSubmit() {
        click(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_SUBMIT_TEST_TAG, useSemanticsAction = true)
    }

    fun clickListSwitch(listId: Long) {
        click(ShowDetailsTestTags.traktListItemSwitch(listId), useSemanticsAction = true)
    }

    fun assertListSwitchIsChecked(listId: Long) {
        composeTestRule.onNodeWithTag(
            ShowDetailsTestTags.traktListItemSwitch(listId),
            useUnmergedTree = true,
        ).assertIsOn()
    }

    fun assertListSwitchIsUnchecked(listId: Long) {
        composeTestRule.onNodeWithTag(
            ShowDetailsTestTags.traktListItemSwitch(listId),
            useUnmergedTree = true,
        ).assertIsOff()
    }

    fun assertTraktListShowCountText(listId: Long, expectedText: String) {
        composeTestRule.onNodeWithTag(
            ShowDetailsTestTags.traktListItemShowCount(listId),
            useUnmergedTree = true,
        ).assert(hasText(expectedText))
    }
}
