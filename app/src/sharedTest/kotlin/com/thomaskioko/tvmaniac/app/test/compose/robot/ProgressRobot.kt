package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.progress.ProgressTestTags
import com.thomaskioko.tvmaniac.testtags.upnext.UpNextTestTags

internal class ProgressRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertProgressScreenDisplayed() {
        assertDisplayed(ProgressTestTags.SCREEN_TEST_TAG)
    }

    fun assertUpNextEmptyStateDisplayed() {
        assertDisplayed(UpNextTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertEpisodeRowDisplayed(traktId: Long) {
        assertExists(UpNextTestTags.episodeRow(traktId))
    }

    fun assertEpisodeRowDoesNotExist(traktId: Long) {
        assertDoesNotExist(UpNextTestTags.episodeRow(traktId))
    }

    fun assertEpisodeMetaDisplayed(traktId: Long, formattedEpisodeNumber: String) {
        assertExists(UpNextTestTags.episodeMeta(traktId, formattedEpisodeNumber))
    }

    fun assertProgressCountDisplayed(traktId: Long, count: String) {
        assertExists(UpNextTestTags.progressCount(traktId, count))
    }

    fun clickWatchedButton(traktId: Long) {
        click(UpNextTestTags.watchedButton(traktId), useSemanticsAction = true)
    }

    fun clickEpisodeRow(traktId: Long) {
        click(UpNextTestTags.episodeRow(traktId), useSemanticsAction = true)
    }

    fun clickCalendarTab() {
        click(ProgressTestTags.CALENDAR_TAB, useSemanticsAction = true)
    }

    fun clickUpNextTab() {
        click(ProgressTestTags.UPNEXT_TAB, useSemanticsAction = true)
    }
}
