package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.calendar.CalendarTestTags

@OptIn(ExperimentalTestApi::class)
internal class CalendarRobot(composeUi: ComposeUiTest) : BaseRobot<CalendarRobot>(composeUi) {

    fun assertCalendarScreenDisplayed() = apply {
        assertDisplayed(CalendarTestTags.SCREEN_TEST_TAG)
    }

    fun assertLoadingIndicatorDisplayed() = apply {
        assertDisplayed(CalendarTestTags.LOADING_INDICATOR)
    }

    fun assertLoadingIndicatorDoesNotExist() = apply {
        assertDoesNotExist(CalendarTestTags.LOADING_INDICATOR)
    }

    fun assertLoggedOutStateDisplayed() = apply {
        assertDisplayed(CalendarTestTags.LOGGED_OUT_STATE_TEST_TAG)
    }

    fun assertEmptyStateDisplayed() = apply {
        assertDisplayed(CalendarTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertWeekLabelDisplayed(text: String) = apply {
        assertDisplayed(CalendarTestTags.WEEK_LABEL)
        assertTextEquals(CalendarTestTags.WEEK_LABEL, text)
    }

    fun assertDateHeaderDisplayed(text: String) = apply {
        val tag = CalendarTestTags.dateHeader(text)
        assertDisplayed(tag)
        assertTextEquals(tag, text)
    }

    fun assertAdditionalEpisodesCountDisplayed(episodeTraktId: Long, expectedText: String) = apply {
        val tag = CalendarTestTags.additionalEpisodesCount(episodeTraktId)
        assertDisplayed(tag)
        assertNodeHasText(tag, expectedText)
    }

    fun scrollToAdditionalEpisodesCount(episodeTraktId: Long) = apply {
        val tag = CalendarTestTags.additionalEpisodesCount(episodeTraktId)
        scrollDownUntilTag(CalendarTestTags.SCREEN_TEST_TAG, tag)
    }

    fun clickNextWeek() = apply {
        click(CalendarTestTags.NEXT_WEEK_BUTTON)
    }

    fun assertEpisodeCardDisplayed(episodeId: Long) = apply {
        val tag = CalendarTestTags.episodeCard(episodeId)
        assertExists(tag)
    }

    fun scrollToEpisodeCard(episodeId: Long) = apply {
        val tag = CalendarTestTags.episodeCard(episodeId)
        scrollDownUntilTag(CalendarTestTags.SCREEN_TEST_TAG, tag)
    }

    fun assertEpisodeCardDoesNotExist(episodeId: Long) = apply {
        assertDoesNotExist(CalendarTestTags.episodeCard(episodeId))
    }
}
