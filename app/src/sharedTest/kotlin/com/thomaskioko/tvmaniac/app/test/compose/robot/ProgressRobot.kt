package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.progress.ProgressTestTags
import com.thomaskioko.tvmaniac.testtags.upnext.UpNextTestTags

@OptIn(ExperimentalTestApi::class)
internal class ProgressRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

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
        assertNodeHasText(UpNextTestTags.episodeRow(traktId), formattedEpisodeNumber)
    }

    fun assertProgressCountDisplayed(traktId: Long, count: String) {
        assertNodeHasText(UpNextTestTags.episodeRow(traktId), count)
    }

    fun clickWatchedButton(traktId: Long) {
        click(UpNextTestTags.watchedButton(traktId))
    }

    fun clickEpisodeRow(traktId: Long) {
        click(UpNextTestTags.episodeRow(traktId))
    }

    fun clickCalendarTab() {
        click(ProgressTestTags.CALENDAR_TAB)
    }

    fun clickUpNextTab() {
        click(ProgressTestTags.UPNEXT_TAB)
    }
}
