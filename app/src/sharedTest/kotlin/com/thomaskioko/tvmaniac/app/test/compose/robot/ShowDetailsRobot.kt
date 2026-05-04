package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags

@OptIn(ExperimentalTestApi::class)
internal class ShowDetailsRobot(composeUi: ComposeUiTest) : BaseRobot<ShowDetailsRobot>(composeUi) {

    fun assertShowDetailsDisplayed() = apply {
        assertDisplayed(ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG)
    }

    fun assertTrackButtonDisplayed() = apply {
        assertDisplayed(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun assertStopTrackingButtonDisplayed() = apply {
        assertDisplayed(ShowDetailsTestTags.STOP_TRACKING_BUTTON_TEST_TAG)
    }

    fun assertSeasonChipDisplayed(seasonNumber: Long) = apply {
        val tag = ShowDetailsTestTags.seasonChip(seasonNumber)
        scrollDownUntilTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.WATCH_PROGRESS_SECTION_TEST_TAG,
        )
        waitForIdle()
        scrollToListTag(
            listTag = ShowDetailsTestTags.WATCH_PROGRESS_LIST_TEST_TAG,
            itemTag = tag,
        )
        assertExists(tag)
    }

    fun clickTrackButton() = apply {
        click(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun clickContinueTrackingMarkWatched(episodeId: Long) = apply {
        val tag = ShowDetailsTestTags.continueTrackingMarkWatchedButton(episodeId)
        scrollDownUntilTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.CONTINUE_TRACKING_SECTION_TEST_TAG,
        )
        waitForIdle()
        scrollToListTag(
            listTag = ShowDetailsTestTags.CONTINUE_TRACKING_LIST_TEST_TAG,
            itemTag = tag,
        )
        click(tag)
    }

    fun assertContinueTrackingEpisodeDisplayed(episodeId: Long) = apply {
        val tag = ShowDetailsTestTags.continueTrackingMarkWatchedButton(episodeId)
        scrollDownUntilTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.CONTINUE_TRACKING_SECTION_TEST_TAG,
        )
        waitForIdle()
        scrollToListTag(
            listTag = ShowDetailsTestTags.CONTINUE_TRACKING_LIST_TEST_TAG,
            itemTag = tag,
        )
        assertExists(tag)
    }

    fun clickAddToListButton() = apply {
        val tag = ShowDetailsTestTags.ADD_TO_LIST_BUTTON_TEST_TAG
        scrollDownUntilTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = tag,
        )
        click(tag)
    }

    fun assertLoginPromptDisplayed() = apply {
        assertExists(ShowDetailsTestTags.LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun assertLoginPromptDoesNotExist() = apply {
        assertDoesNotExist(ShowDetailsTestTags.LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun confirmLoginPrompt() = apply {
        click(ShowDetailsTestTags.LOGIN_REQUIRED_DIALOG_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickSeasonChip(seasonNumber: Long): SeasonDetailsRobot {
        val tag = ShowDetailsTestTags.seasonChip(seasonNumber)
        scrollDownUntilTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.WATCH_PROGRESS_SECTION_TEST_TAG,
        )
        waitForIdle()
        scrollToListTag(
            listTag = ShowDetailsTestTags.WATCH_PROGRESS_LIST_TEST_TAG,
            itemTag = tag,
        )
        click(tag)
        return SeasonDetailsRobot(composeUi)
    }

    fun clickBackButton() = apply {
        click(ShowDetailsTestTags.BACK_BUTTON_TEST_TAG, useUnmergedTree = true)
    }

    fun assertErrorStateDisplayed() = apply {
        assertDisplayed(ShowDetailsTestTags.ERROR_RETRY_BUTTON_TEST_TAG)
    }

    fun clickRetryButton() = apply {
        click(ShowDetailsTestTags.ERROR_RETRY_BUTTON_TEST_TAG)
    }

    fun clickRefreshButton() = apply {
        assertDisplayed(ShowDetailsTestTags.REFRESH_BUTTON_TEST_TAG)
        click(ShowDetailsTestTags.REFRESH_BUTTON_TEST_TAG)
    }

    fun assertCastListDisplayed() = apply {
        scrollDownUntilTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.CAST_LIST_TEST_TAG,
        )
        assertDisplayed(ShowDetailsTestTags.CAST_LIST_TEST_TAG)
    }

    fun assertTrailersListDisplayed() = apply {
        scrollDownUntilTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.TRAILERS_LIST_TEST_TAG,
        )
        assertDisplayed(ShowDetailsTestTags.TRAILERS_LIST_TEST_TAG)
    }

    fun assertSimilarShowsListDisplayed() = apply {
        scrollDownUntilTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.SIMILAR_SHOWS_LIST_TEST_TAG,
        )
        assertDisplayed(ShowDetailsTestTags.SIMILAR_SHOWS_LIST_TEST_TAG)
    }

    fun assertContinueTrackingSectionDisplayed() = apply {
        scrollDownUntilTag(
            listTag = ShowDetailsTestTags.SHOW_DETAILS_SCREEN_TEST_TAG,
            itemTag = ShowDetailsTestTags.CONTINUE_TRACKING_SECTION_TEST_TAG,
        )
        assertDisplayed(ShowDetailsTestTags.CONTINUE_TRACKING_SECTION_TEST_TAG)
    }

    fun assertListSheetDisplayed() = apply {
        assertDisplayed(ShowDetailsTestTags.LIST_SHEET_TEST_TAG)
    }

    fun assertListSheetDoesNotExist() = apply {
        assertDoesNotExist(ShowDetailsTestTags.LIST_SHEET_TEST_TAG)
    }

    fun assertTraktListItemDisplayed(listId: Long) = apply {
        assertExists(ShowDetailsTestTags.traktListItem(listId))
    }

    fun clickCloseListSheetButton() = apply {
        click(ShowDetailsTestTags.LIST_SHEET_CLOSE_BUTTON_TEST_TAG)
    }

    fun clickCreateListButton() = apply {
        click(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_BUTTON_TEST_TAG)
    }

    fun assertCreateListFieldDisplayed() = apply {
        assertExists(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG)
    }

    fun assertCreateListFieldDoesNotExist() = apply {
        assertDoesNotExist(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG)
    }

    fun typeCreateListName(name: String) = apply {
        inputText(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_INPUT_TEST_TAG, name)
    }

    fun clickCreateListSubmit() = apply {
        click(ShowDetailsTestTags.LIST_SHEET_CREATE_LIST_SUBMIT_TEST_TAG)
    }

    fun clickListSwitch(listId: Long) = apply {
        click(ShowDetailsTestTags.traktListItemSwitch(listId))
    }

    fun assertListSwitchIsChecked(listId: Long) = apply {
        assertChecked(ShowDetailsTestTags.traktListItemSwitch(listId))
    }

    fun assertListSwitchIsUnchecked(listId: Long) = apply {
        assertUnchecked(ShowDetailsTestTags.traktListItemSwitch(listId))
    }

    fun assertTraktListShowCountText(listId: Long, expectedText: String) = apply {
        assertNodeHasText(ShowDetailsTestTags.traktListItemShowCount(listId), expectedText)
    }
}
