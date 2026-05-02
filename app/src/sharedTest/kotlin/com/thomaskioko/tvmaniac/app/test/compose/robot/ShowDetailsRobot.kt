package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags

@OptIn(ExperimentalTestApi::class)
internal class ShowDetailsRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

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
        click(ShowDetailsTestTags.seasonChip(seasonNumber))
        return SeasonDetailsRobot(composeUi)
    }

    fun clickBackButton() {
        click(ShowDetailsTestTags.BACK_BUTTON_TEST_TAG, useUnmergedTree = true)
    }

    fun assertErrorStateDisplayed() {
        assertDisplayed(ShowDetailsTestTags.ERROR_RETRY_BUTTON_TEST_TAG)
    }

    fun clickRetryButton() {
        click(ShowDetailsTestTags.ERROR_RETRY_BUTTON_TEST_TAG)
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
        assertNotDisplayed(ShowDetailsTestTags.LIST_SHEET_TEST_TAG)
    }

    fun assertTraktListItemDisplayed(listId: Long) {
        assertExists(ShowDetailsTestTags.traktListItem(listId))
    }

    fun clickCloseListSheetButton() {
        click(ShowDetailsTestTags.LIST_SHEET_CLOSE_BUTTON_TEST_TAG)
    }

    fun clickCreateListButton() {
        click(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_BUTTON_TEST_TAG)
    }

    fun assertCreateListFieldDisplayed() {
        assertExists(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG)
    }

    fun assertCreateListFieldDoesNotExist() {
        assertDoesNotExist(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG)
    }

    fun typeCreateListName(name: String) {
        inputText(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG, name)
    }

    fun clickCreateListSubmit() {
        click(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_SUBMIT_TEST_TAG)
    }

    fun clickListSwitch(listId: Long) {
        click(ShowDetailsTestTags.traktListItemSwitch(listId))
    }

    fun assertListSwitchIsChecked(listId: Long) {
        assertChecked(ShowDetailsTestTags.traktListItemSwitch(listId))
    }

    fun assertListSwitchIsUnchecked(listId: Long) {
        assertUnchecked(ShowDetailsTestTags.traktListItemSwitch(listId))
    }

    fun assertTraktListShowCountText(listId: Long, expectedText: String) {
        assertNodeHasText(ShowDetailsTestTags.traktListItemShowCount(listId), expectedText)
    }
}
