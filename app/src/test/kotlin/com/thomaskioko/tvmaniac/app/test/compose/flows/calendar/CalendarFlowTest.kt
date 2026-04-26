package com.thomaskioko.tvmaniac.app.test.compose.flows.calendar

import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import com.thomaskioko.tvmaniac.testing.integration.ui.NetworkResponse
import com.thomaskioko.tvmaniac.testing.integration.ui.stubFixture
import org.junit.Before
import kotlin.test.Test

internal class CalendarFlowTest : BaseAppRobolectricTest() {

    @Before
    fun setUp() {
        scenarios.stubDiscoverBrowse()
    }

    @Test
    fun `should show login prompt when user is not logged in`() {
        homeRobot.clickProgressTab()
        progressRobot.verifyProgressScreenIsShown()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyLoggedOutStateIsShown()
        calendarRobot.verifyTextShown("Login to Trakt to see your calendar")
    }

    @Test
    fun `should show upcoming episodes when user is logged in`() {
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
    fun `should show empty state when no episodes are scheduled`() {
        scenarios.auth.stubLoggedInUser()

        environment.stubber.stub(path = "/calendars/my/shows/2026-04-19/7", response = NetworkResponse.Success("[]"))

        homeRobot.clickProgressTab()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyEmptyStateIsShown()
        calendarRobot.verifyTextShown("Nothing to see here")
    }

    @Test
    fun `should load episodes for next week when next button is clicked`() {
        scenarios.auth.stubLoggedInUser()

        // Stub current week (Offset 0)
        environment.stubber.stubFixture(
            path = "/calendars/my/shows/2026-04-19/7",
            fixturePath = "trakt/calendar_breaking_bad.json",
        )

        // Stub next week (Offset 1)
        environment.stubber.stubFixture(
            path = "/calendars/my/shows/2026-04-26/7",
            fixturePath = "trakt/calendar_game_of_thrones.json",
        )

        homeRobot.clickProgressTab()
        progressRobot.verifyProgressScreenIsShown()
        progressRobot.clickCalendarTab()
        calendarRobot.verifyCalendarScreenIsShown()
        calendarRobot.verifyTextShown("Breaking Bad")
        // Navigate to next week
        calendarRobot.clickNextWeek()
        // Verify new content is shown and old content is gone
        calendarRobot.verifyTextShown("Game of Thrones")
        calendarRobot.verifyEpisodeCardIsHidden(73640L)
    }

    @Test
    fun `should show error snackbar when calendar fetch fails`() {
        scenarios.auth.stubLoggedInUser()

        // Stub non-retryable error
        environment.stubber.stub(path = "/calendars/my/shows/2026-04-19/7", response = NetworkResponse.Error(404, "Not Found"))

        homeRobot.clickProgressTab()
        progressRobot.clickCalendarTab()
        // Verify snackbar appears with "Resource not found."
        calendarRobot.verifyTextShown("Resource not found", substring = true)
    }
}
