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
    fun shouldShowLoginPromptWhenUserIsNotLoggedIn() {
        discoverRobot.verifyDiscoverScreenIsShown()

        homeRobot.clickProgressTab()

        progressRobot.verifyProgressScreenIsShown()
        progressRobot.clickCalendarTab()

        calendarRobot.verifyLoggedOutStateIsShown()
        calendarRobot.verifyTextShown("Login to Trakt to see your calendar")
    }

    @Test
    fun shouldShowUpcomingEpisodesWhenUserIsLoggedIn() {
        discoverRobot.verifyDiscoverScreenIsShown()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubWeek()

        homeRobot.clickProgressTab()

        progressRobot.clickCalendarTab()

        calendarRobot.verifyCalendarScreenIsShown()
        calendarRobot.verifyDateHeader("Today, Apr 19, 2026")
        calendarRobot.verifyTextShown("Breaking Bad")
        calendarRobot.verifyAdditionalEpisodesCount(episodeTraktId = 73640L, expectedText = "+1 episodes")
    }

    @Test
    fun shouldShowEmptyStateWhenNoEpisodesAreScheduled() {
        discoverRobot.verifyDiscoverScreenIsShown()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubEmptyWeek()

        homeRobot.clickProgressTab()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyEmptyStateIsShown()
        calendarRobot.verifyTextShown("Nothing to see here")
    }

    @Test
    fun shouldLoadEpisodesForNextWeekWhenNextButtonIsClicked() {
        discoverRobot.verifyDiscoverScreenIsShown()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubWeek()
        scenarios.calendar.stubWeek(weekStart = TEST_NEXT_WEEK)

        homeRobot.clickProgressTab()

        progressRobot.verifyProgressScreenIsShown()

        progressRobot.clickCalendarTab()

        calendarRobot.verifyCalendarScreenIsShown()
        calendarRobot.verifyWeekLabel("Apr 19, 2026 - Apr 25, 2026")
        calendarRobot.verifyDateHeader("Today, Apr 19, 2026")
        calendarRobot.verifyTextShown("Breaking Bad")
        calendarRobot.clickNextWeek()
        calendarRobot.verifyWeekLabel("Apr 26, 2026 - May 2, 2026")
        calendarRobot.verifyDateHeader("Sunday, Apr 26, 2026")
        calendarRobot.verifyTextShown("Game of Thrones")
        calendarRobot.verifyEpisodeCardIsHidden(73640L)
    }

    @Test
    fun shouldShowErrorSnackbarWhenCalendarFetchFails() {
        discoverRobot.verifyDiscoverScreenIsShown()

        scenarios.signInAndDismissRationale()

        scenarios.calendar.stubWeekError()

        homeRobot.clickProgressTab()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyWeekLabel("Apr 19, 2026 - Apr 25, 2026")
        calendarRobot.verifyTextShown("Resource not found", substring = true)
    }
}
