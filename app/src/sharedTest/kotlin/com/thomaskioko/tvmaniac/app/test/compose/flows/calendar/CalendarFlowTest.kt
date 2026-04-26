package com.thomaskioko.tvmaniac.app.test.compose.flows.calendar

import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testing.integration.ui.NetworkResponse
import com.thomaskioko.tvmaniac.testing.integration.ui.stubFixture
import org.junit.Before
import org.junit.Test

internal class CalendarFlowTest : BaseAppFlowTest() {

    @Before
    fun setUp() {
        scenarios.stubDiscoverBrowse()
    }

    @Test
    fun shouldShowLoginPromptWhenUserIsNotLoggedIn() {
        homeRobot.clickProgressTab()
        progressRobot.verifyProgressScreenIsShown()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyLoggedOutStateIsShown()
        calendarRobot.verifyTextShown("Login to Trakt to see your calendar")
    }

    @Test
    fun shouldShowUpcomingEpisodesWhenUserIsLoggedIn() {
        scenarios.auth.stubLoggedInUser()

        environment.stubber.stubFixture(
            path = "/calendars/my/shows/2026-04-19/7",
            fixturePath = "trakt/calendar_breaking_bad.json",
        )

        homeRobot.clickProgressTab()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyCalendarScreenIsShown()
        calendarRobot.verifyTextShown("Breaking Bad")
    }

    @Test
    fun shouldShowEmptyStateWhenNoEpisodesAreScheduled() {
        scenarios.auth.stubLoggedInUser()

        environment.stubber.stub(path = "/calendars/my/shows/2026-04-19/7", response = NetworkResponse.Success("[]"))

        homeRobot.clickProgressTab()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyEmptyStateIsShown()
        calendarRobot.verifyTextShown("Nothing to see here")
    }

    @Test
    fun shouldLoadEpisodesForNextWeekWhenNextButtonIsClicked() {
        scenarios.auth.stubLoggedInUser()

        environment.stubber.stubFixture(
            path = "/calendars/my/shows/2026-04-19/7",
            fixturePath = "trakt/calendar_breaking_bad.json",
        )

        environment.stubber.stubFixture(
            path = "/calendars/my/shows/2026-04-26/7",
            fixturePath = "trakt/calendar_game_of_thrones.json",
        )

        homeRobot.clickProgressTab()
        progressRobot.verifyProgressScreenIsShown()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyCalendarScreenIsShown()
        calendarRobot.verifyTextShown("Breaking Bad")
        calendarRobot.clickNextWeek()
        calendarRobot.verifyTextShown("Game of Thrones")
        calendarRobot.verifyEpisodeCardIsHidden(73640L)
    }

    @Test
    fun shouldShowErrorSnackbarWhenCalendarFetchFails() {
        scenarios.auth.stubLoggedInUser()

        environment.stubber.stub(path = "/calendars/my/shows/2026-04-19/7", response = NetworkResponse.Error(404, "Not Found"))

        homeRobot.clickProgressTab()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyTextShown("Resource not found", substring = true)
    }
}
