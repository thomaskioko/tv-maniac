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

    fun verifyShowDetailsIsShown() {
        verifyTagShown(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun verifyTrackButtonIsShown() {
        verifyTagShown(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun verifyStopTrackingButtonIsShown() {
        verifyTagShown(ShowDetailsTestTags.STOP_TRACKING_BUTTON_TEST_TAG)
    }

    fun verifySeasonChipIsShown(seasonNumber: Long) {
        verifyTagExists(ShowDetailsTestTags.seasonChip(seasonNumber))
    }

    fun clickTrackButton() {
        click(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun clickContinueTrackingMarkWatched(episodeId: Long) {
        click(ShowDetailsTestTags.continueTrackingMarkWatchedButton(episodeId))
    }

    fun verifyContinueTrackingEpisodeIsShown(episodeId: Long) {
        verifyTagExists(ShowDetailsTestTags.continueTrackingMarkWatchedButton(episodeId))
    }

    fun clickAddToListButton() {
        click(ShowDetailsTestTags.ADD_TO_LIST_BUTTON_TEST_TAG)
    }

    fun verifyLoginPromptIsShown() {
        verifyTagExists(ShowDetailsTestTags.LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun verifyLoginPromptIsHidden() {
        verifyTagHidden(ShowDetailsTestTags.LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun confirmLoginPrompt() {
        click(ShowDetailsTestTags.LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickSeasonChip(seasonNumber: Long): SeasonDetailsRobot {
        click(ShowDetailsTestTags.seasonChip(seasonNumber), useSemanticsAction = true)
        return SeasonDetailsRobot(composeTestRule)
    }

    fun verifyErrorStateIsShown() {
        verifyTagShown(ShowDetailsTestTags.ERROR_RETRY_BUTTON_TEST_TAG)
    }

    fun clickRetryButton() {
        click(ShowDetailsTestTags.ERROR_RETRY_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickRefreshButton() {
        verifyTagShown(ShowDetailsTestTags.REFRESH_BUTTON_TEST_TAG)
        click(ShowDetailsTestTags.REFRESH_BUTTON_TEST_TAG)
    }

    fun verifyCastListIsShown() {
        verifyTagShown(ShowDetailsTestTags.CAST_LIST_TEST_TAG)
    }

    fun verifyTrailersListIsShown() {
        verifyTagShown(ShowDetailsTestTags.TRAILERS_LIST_TEST_TAG)
    }

    fun verifySimilarShowsListIsShown() {
        verifyTagShown(ShowDetailsTestTags.SIMILAR_SHOWS_LIST_TEST_TAG)
    }

    fun verifyContinueTrackingSectionIsShown() {
        verifyTagShown(ShowDetailsTestTags.CONTINUE_TRACKING_SECTION_TEST_TAG)
    }

    fun verifyListSheetIsShown() {
        verifyTagShown(ShowDetailsTestTags.LIST_SHEET_TEST_TAG)
    }

    fun verifyListSheetIsHidden() {
        composeTestRule.onNodeWithTag(
            ShowDetailsTestTags.LIST_SHEET_TEST_TAG,
            useUnmergedTree = true,
        ).assertIsNotDisplayed()
    }

    fun verifyTraktListItemIsShown(listId: Long) {
        verifyTagExists(ShowDetailsTestTags.traktListItem(listId))
    }

    fun clickCloseListSheetButton() {
        click(ShowDetailsTestTags.LIST_SHEET_CLOSE_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun clickCreateListButton() {
        click(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_BUTTON_TEST_TAG, useSemanticsAction = true)
    }

    fun verifyCreateListFieldIsShown() {
        verifyTagExists(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG)
    }

    fun verifyCreateListFieldIsHidden() {
        verifyTagHidden(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG)
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

    fun verifyListSwitchIsChecked(listId: Long) {
        composeTestRule.onNodeWithTag(
            ShowDetailsTestTags.traktListItemSwitch(listId),
            useUnmergedTree = true,
        ).assertIsOn()
    }

    fun verifyListSwitchIsUnchecked(listId: Long) {
        composeTestRule.onNodeWithTag(
            ShowDetailsTestTags.traktListItemSwitch(listId),
            useUnmergedTree = true,
        ).assertIsOff()
    }

    fun verifyTraktListShowCountText(listId: Long, expectedText: String) {
        composeTestRule.onNodeWithTag(
            ShowDetailsTestTags.traktListItemShowCount(listId),
            useUnmergedTree = true,
        ).assert(hasText(expectedText))
    }
}
