package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.robot.BaseRobot
import com.thomaskioko.tvmaniac.testtags.progress.ProgressTestTags
import com.thomaskioko.tvmaniac.testtags.upnext.UpNextTestTags

internal class ProgressRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifyProgressScreenIsShown() {
        verifyTagShown(ProgressTestTags.SCREEN_TEST_TAG)
    }

    fun verifyUpNextEmptyStateIsShown() {
        verifyTagShown(UpNextTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun verifyEpisodeRowIsShown(traktId: Long) {
        verifyTagExists(UpNextTestTags.episodeRow(traktId))
    }

    fun verifyEpisodeRowIsHidden(traktId: Long) {
        verifyTagHidden(UpNextTestTags.episodeRow(traktId))
    }

    fun clickCalendarTab() {
        click(ProgressTestTags.CALENDAR_TAB, useSemanticsAction = true)
    }

    fun clickUpNextTab() {
        click(ProgressTestTags.UPNEXT_TAB, useSemanticsAction = true)
    }
}
