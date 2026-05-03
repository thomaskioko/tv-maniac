package com.thomaskioko.tvmaniac.app.test.compose.flows.calendar

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_NEXT_WEEK
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import org.junit.Test

internal class CalendarFlowTest : BaseAppFlowTest() {

    @Test
    fun givenUnauthenticatedUser_whenNavigatesToCalendar_thenShowsLoginPrompt() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertProgressScreenDisplayed()
            .clickCalendarTab()
            .assertCalendarTabSelected()

        calendarRobot
            .assertLoggedOutStateDisplayed()
            .assertTextDisplayed("Login to Trakt to see your calendar")
    }

    @Test
    fun authenticatedUserCalendarJourney() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot
            .assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubWeek()
        scenarios.calendar.stubWeek(weekStart = TEST_NEXT_WEEK)

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertProgressScreenDisplayed()
            .clickCalendarTab()
            .assertCalendarTabSelected()

        // Assert Current Week
        calendarRobot
            .assertCalendarScreenDisplayed()
            .assertWeekLabelDisplayed("Apr 19, 2026 - Apr 25, 2026")
            .assertDateHeaderDisplayed("Today, Apr 19, 2026")
            .assertTextDisplayed("Breaking Bad")
            .scrollToAdditionalEpisodesCount(episodeTraktId = 73640L)
            .assertAdditionalEpisodesCountDisplayed(episodeTraktId = 73640L, expectedText = "+1 episodes")
            .clickNextWeek()
            .assertWeekLabelDisplayed("Apr 26, 2026 - May 2, 2026")
            .assertDateHeaderDisplayed("Sunday, Apr 26, 2026")
            .assertTextDisplayed("Game of Thrones")
            .assertEpisodeCardDoesNotExist(73640L)
    }

    @Test
    fun givenAuthenticatedUser_whenNoEpisodesScheduled_thenShowsEmptyState() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubEmptyWeek()

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .clickCalendarTab()
            .assertCalendarTabSelected()

        calendarRobot
            .assertEmptyStateDisplayed()
            .assertTextDisplayed("Nothing to see here")
    }

    @Test
    fun givenAuthenticatedUser_whenCalendarFetchFails_thenShowsErrorSnackbar() = runAppFlowTest {
        scenarios.discover.stubBrowseGraph()

        discoverRobot.assertDiscoverScreenDisplayed()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubWeekError()

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .clickCalendarTab()
            .assertCalendarTabSelected()

        calendarRobot
            .assertWeekLabelDisplayed("Apr 19, 2026 - Apr 25, 2026")
            .assertTextDisplayed("Resource not found", substring = true)
    }
}
