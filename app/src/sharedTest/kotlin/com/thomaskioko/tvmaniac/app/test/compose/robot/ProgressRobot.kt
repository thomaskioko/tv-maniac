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

    fun assertLoadingIndicatorDisplayed() = apply {
        assertDisplayed(ProgressTestTags.PROGRESS_INDICATOR)
    }

    fun assertLoadingIndicatorDoesNotExist() = apply {
        assertDoesNotExist(ProgressTestTags.PROGRESS_INDICATOR)
    }

    fun assertUpNextTabSelected() = apply {
        assertSelected(ProgressTestTags.UPNEXT_TAB)
    }

    fun assertCalendarTabSelected() = apply {
        assertSelected(ProgressTestTags.CALENDAR_TAB)
    }

    fun assertUpNextPageDisplayed() = apply {
        assertDisplayed(UpNextTestTags.LIST_TEST_TAG)
    }

    fun scrollToUpNextEpisode(traktId: Long) = apply {
        val tag = UpNextTestTags.episodeRow(traktId)
        scrollDownUntilTag(UpNextTestTags.LIST_TEST_TAG, tag)
    }

    fun assertUpNextEpisodeDisplayed(traktId: Long) = apply {
        assertDisplayed(UpNextTestTags.episodeRow(traktId))
    }

    fun assertUpNextEpisodeDoesNotExist(traktId: Long) = apply {
        assertDoesNotExist(UpNextTestTags.episodeRow(traktId))
    }

    fun assertUpNextEpisodeMetaDisplayed(traktId: Long, formattedEpisodeNumber: String) = apply {
        assertNodeHasText(UpNextTestTags.episodeRow(traktId), formattedEpisodeNumber)
    }

    fun assertUpNextProgressCountDisplayed(traktId: Long, count: String) = apply {
        assertNodeHasText(UpNextTestTags.episodeRow(traktId), count)
    }

    fun clickUpNextWatchedButton(traktId: Long) = apply {
        click(UpNextTestTags.watchedButton(traktId))
    }

    fun clickUpNextEpisodeRow(traktId: Long): SeasonDetailsRobot {
        click(UpNextTestTags.episodeRow(traktId))
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
