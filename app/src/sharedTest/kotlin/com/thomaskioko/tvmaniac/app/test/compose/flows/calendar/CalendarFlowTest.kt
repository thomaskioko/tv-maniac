package com.thomaskioko.tvmaniac.app.test.compose.flows.calendar

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_NEXT_WEEK
import org.junit.Before
import org.junit.Test

internal class CalendarFlowTest : BaseAppFlowTest() {

    @Before
    fun setUp() {
        scenarios.discover.stubBrowseGraph()
    }

    @Test
    fun givenUnauthenticatedUser_whenNavigatesToCalendar_thenShowsLoginPrompt() {
        discoverRobot.assertDiscoverScreenDisplayed()

        homeRobot.clickProgressTab()

        progressRobot.assertProgressScreenDisplayed()
        progressRobot.clickCalendarTab()

        calendarRobot.assertLoggedOutStateDisplayed()
        calendarRobot.assertTextDisplayed("Login to Trakt to see your calendar")
    }

    @Test
    fun givenAuthenticatedUser_whenNavigatesToCalendar_thenShowsUpcomingEpisodes() {
        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubWeek()

        homeRobot.clickProgressTab()

        progressRobot.clickCalendarTab()

        calendarRobot.assertCalendarScreenDisplayed()
        calendarRobot.assertDateHeaderDisplayed("Today, Apr 19, 2026")
        calendarRobot.assertTextDisplayed("Breaking Bad")
        calendarRobot.assertAdditionalEpisodesCountDisplayed(episodeTraktId = 73640L, expectedText = "+1 episodes")
    }

    @Test
    fun givenAuthenticatedUser_whenNoEpisodesScheduled_thenShowsEmptyState() {
        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubEmptyWeek()

        homeRobot.clickProgressTab()
        progressRobot.clickCalendarTab()
        calendarRobot.assertEmptyStateDisplayed()
        calendarRobot.assertTextDisplayed("Nothing to see here")
    }

    @Test
    fun givenAuthenticatedUser_whenNextWeekClicked_thenLoadsNextWeekEpisodes() {
        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubWeek()
        scenarios.calendar.stubWeek(weekStart = TEST_NEXT_WEEK)

        homeRobot.clickProgressTab()

        progressRobot.assertProgressScreenDisplayed()

        progressRobot.clickCalendarTab()

        calendarRobot.assertCalendarScreenDisplayed()
        calendarRobot.assertWeekLabelDisplayed("Apr 19, 2026 - Apr 25, 2026")
        calendarRobot.assertDateHeaderDisplayed("Today, Apr 19, 2026")
        calendarRobot.assertTextDisplayed("Breaking Bad")
        calendarRobot.clickNextWeek()
        calendarRobot.assertWeekLabelDisplayed("Apr 26, 2026 - May 2, 2026")
        calendarRobot.assertDateHeaderDisplayed("Sunday, Apr 26, 2026")
        calendarRobot.assertTextDisplayed("Game of Thrones")
        calendarRobot.assertEpisodeCardDoesNotExist(73640L)
    }

    @Test
    fun givenAuthenticatedUser_whenCalendarFetchFails_thenShowsErrorSnackbar() {
        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubWeekError()

        homeRobot.clickProgressTab()
        progressRobot.clickCalendarTab()
        calendarRobot.assertWeekLabelDisplayed("Apr 19, 2026 - Apr 25, 2026")
        calendarRobot.assertTextDisplayed("Resource not found", substring = true)
    }
}
