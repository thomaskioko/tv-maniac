package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.calendar.CalendarTestTags

@OptIn(ExperimentalTestApi::class)
internal class CalendarRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

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
        assertTextEquals(CalendarTestTags.WEEK_LABEL, text)
    }

    fun assertDateHeaderDisplayed(text: String) {
        assertDisplayed(CalendarTestTags.DATE_HEADER)
        assertTextEquals(CalendarTestTags.DATE_HEADER, text)
    }

    fun assertAdditionalEpisodesCountDisplayed(episodeTraktId: Long, expectedText: String) {
        val tag = CalendarTestTags.additionalEpisodesCount(episodeTraktId)
        assertDisplayed(tag)
        assertTextEquals(tag, expectedText)
    }

    fun clickNextWeek() {
        click(CalendarTestTags.NEXT_WEEK_BUTTON)
    }

    fun assertEpisodeCardDisplayed(episodeId: Long) {
        assertExists(CalendarTestTags.episodeCard(episodeId))
    }

    fun assertEpisodeCardDoesNotExist(episodeId: Long) {
        assertDoesNotExist(CalendarTestTags.episodeCard(episodeId))
    }
}
