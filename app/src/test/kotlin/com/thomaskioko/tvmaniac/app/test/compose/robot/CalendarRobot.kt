package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.robot.BaseRobot
import com.thomaskioko.tvmaniac.testtags.calendar.CalendarTestTags

internal class CalendarRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifyCalendarScreenIsShown() {
        verifyTagShown(CalendarTestTags.SCREEN_TEST_TAG)
    }

    fun verifyLoggedOutStateIsShown() {
        verifyTagShown(CalendarTestTags.LOGGED_OUT_STATE_TEST_TAG)
    }

    fun verifyEmptyStateIsShown() {
        verifyTagShown(CalendarTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun clickNextWeek() {
        click(CalendarTestTags.NEXT_WEEK_BUTTON, useSemanticsAction = true)
    }

    fun verifyEpisodeCardIsShown(episodeId: Long) {
        verifyTagExists(CalendarTestTags.episodeCard(episodeId))
    }

    fun verifyEpisodeCardIsHidden(episodeId: Long) {
        verifyTagHidden(CalendarTestTags.episodeCard(episodeId))
    }
}
