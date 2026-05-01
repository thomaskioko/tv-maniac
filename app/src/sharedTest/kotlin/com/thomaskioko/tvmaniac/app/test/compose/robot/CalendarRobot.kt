package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
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

    fun verifyWeekLabel(text: String) {
        verifyTagShown(CalendarTestTags.WEEK_LABEL)
        composeTestRule.onNodeWithTag(CalendarTestTags.WEEK_LABEL, useUnmergedTree = true)
            .assertTextEquals(text)
    }

    fun verifyDateHeader(text: String) {
        verifyTagShown(CalendarTestTags.DATE_HEADER)
        composeTestRule.onNodeWithTag(CalendarTestTags.DATE_HEADER, useUnmergedTree = true)
            .assertTextEquals(text)
    }

    fun verifyAdditionalEpisodesCount(episodeTraktId: Long, expectedText: String) {
        val tag = CalendarTestTags.additionalEpisodesCount(episodeTraktId)
        verifyTagShown(tag)
        composeTestRule.onNodeWithTag(tag, useUnmergedTree = true)
            .assertTextEquals(expectedText)
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
