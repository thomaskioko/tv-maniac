package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.progress.ProgressTestTags
import com.thomaskioko.tvmaniac.testtags.upnext.UpNextTestTags

@OptIn(ExperimentalTestApi::class)
internal class ProgressRobot(composeUi: ComposeUiTest) : BaseRobot<ProgressRobot>(composeUi) {

    fun assertProgressScreenDisplayed() = apply {
        assertDisplayed(ProgressTestTags.SCREEN_TEST_TAG)
    }

    fun assertUpNextEmptyStateDisplayed() = apply {
        assertDisplayed(UpNextTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertEpisodeRowDisplayed(traktId: Long) = apply {
        val tag = UpNextTestTags.episodeRow(traktId)
        scrollDownUntilTag(UpNextTestTags.LIST_TEST_TAG, tag)
        assertExists(tag)
    }

    fun assertEpisodeRowDoesNotExist(traktId: Long) = apply {
        assertDoesNotExist(UpNextTestTags.episodeRow(traktId))
    }

    fun assertEpisodeMetaDisplayed(traktId: Long, formattedEpisodeNumber: String) = apply {
        val tag = UpNextTestTags.episodeRow(traktId)
        scrollDownUntilTag(UpNextTestTags.LIST_TEST_TAG, tag)
        assertNodeHasText(tag, formattedEpisodeNumber)
    }

    fun assertProgressCountDisplayed(traktId: Long, count: String) = apply {
        val tag = UpNextTestTags.episodeRow(traktId)
        scrollDownUntilTag(UpNextTestTags.LIST_TEST_TAG, tag)
        assertNodeHasText(tag, count)
    }

    fun clickWatchedButton(traktId: Long) = apply {
        val tag = UpNextTestTags.watchedButton(traktId)
        scrollDownUntilTag(UpNextTestTags.LIST_TEST_TAG, tag)
        click(tag)
    }

    fun clickEpisodeRow(traktId: Long): SeasonDetailsRobot {
        val tag = UpNextTestTags.episodeRow(traktId)
        scrollDownUntilTag(UpNextTestTags.LIST_TEST_TAG, tag)
        click(tag)
        return SeasonDetailsRobot(composeUi)
    }

    fun clickCalendarTab() = apply {
        click(ProgressTestTags.CALENDAR_TAB)
    }

    fun clickUpNextTab() = apply {
        click(ProgressTestTags.UPNEXT_TAB)
    }

    fun swipeLeftPager() = apply {
        swipeLeft(ProgressTestTags.HORIZONTAL_PAGER)
        waitForIdle()
    }

    fun swipeRightPager() = apply {
        swipeRight(ProgressTestTags.HORIZONTAL_PAGER)
        waitForIdle()
    }
}
