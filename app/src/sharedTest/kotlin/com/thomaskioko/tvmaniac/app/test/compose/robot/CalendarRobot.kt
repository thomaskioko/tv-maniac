package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.calendar.CalendarTestTags

internal class CalendarRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertCalendarScreenDisplayed() {
        assertDisplayed(CalendarTestTags.SCREEN_TEST_TAG)
    }

    fun assertLoggedOutStateDisplayed() {
        assertDisplayed(CalendarTestTags.LOGGED_OUT_STATE_TEST_TAG)
    }

    fun assertEmptyStateDisplayed() {
        assertDisplayed(CalendarTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertWeekLabelDisplayed(text: String) {
        assertDisplayed(CalendarTestTags.WEEK_LABEL)
        composeTestRule.onNodeWithTag(CalendarTestTags.WEEK_LABEL, useUnmergedTree = true)
            .assertTextEquals(text)
    }

    fun assertDateHeaderDisplayed(text: String) {
        assertDisplayed(CalendarTestTags.DATE_HEADER)
        composeTestRule.onNodeWithTag(CalendarTestTags.DATE_HEADER, useUnmergedTree = true)
            .assertTextEquals(text)
    }

    fun assertAdditionalEpisodesCountDisplayed(episodeTraktId: Long, expectedText: String) {
        val tag = CalendarTestTags.additionalEpisodesCount(episodeTraktId)
        assertDisplayed(tag)
        composeTestRule.onNodeWithTag(tag, useUnmergedTree = true)
            .assertTextEquals(expectedText)
    }

    fun clickNextWeek() {
        click(CalendarTestTags.NEXT_WEEK_BUTTON, useSemanticsAction = true)
    }

    fun assertEpisodeCardDisplayed(episodeId: Long) {
        assertExists(CalendarTestTags.episodeCard(episodeId))
    }

    fun assertEpisodeCardDoesNotExist(episodeId: Long) {
        assertDoesNotExist(CalendarTestTags.episodeCard(episodeId))
    }
}
