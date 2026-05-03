package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_NEXT_WEEK
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import com.thomaskioko.tvmaniac.testtags.notifications.NotificationRationaleTestTags
import org.junit.Test

internal class AuthenticatedUserJourneyTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val betterCallSaulTraktId = 59660L

    @Test
    fun givenAuthenticatedUser_whenSignsIn_thenExploresSyncedSurfacesAndSignsOut() = runAppFlowTest {
        scenarios.stubUnauthenticatedState()
        scenarios.calendar.stubWeek()
        scenarios.calendar.stubWeek(weekStart = TEST_NEXT_WEEK)

        // Verify public content on Discover
        discoverRobot
            .assertLoadingIndicatorDisplayed()
            .assertDiscoverScreenDisplayed()
            .assertFeaturedShowDisplayed(breakingBadTraktId)
            .assertUpNextCardDoesNotExist(breakingBadTraktId)
            .assertFeaturedPagerDisplayed()
            .assertFeaturedShowDisplayed(breakingBadTraktId)
            .swipeFeaturedPagerLeft()
            .assertFeaturedShowDisplayed(betterCallSaulTraktId)
            .swipeFeaturedPagerRight()
            .assertFeaturedShowDisplayed(breakingBadTraktId)

        // Navigate to Profile and verify Sign In CTA
        homeRobot.clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .scrollToSignInButton()
            .assertSignInButtonDisplayed()
            .also { scenarios.stubAuthenticatedSyncOnSignIn() }
            .clickSignInButton()
            .onClick(NotificationRationaleTestTags.DISMISS_BUTTON)
            .assertUserCardDisplayed(slug = TEST_PROFILE_SLUG)

        // Verify synced surfaces appear after auth
        homeRobot
            .clickLibraryTab()
            .assertTabSelected(HomeTestTags.LIBRARY_TAB)

        libraryRobot
            .scrollToShowRow(breakingBadTraktId)
            .assertShowRowDisplayed(breakingBadTraktId)

        homeRobot
            .clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertUpNextTabSelected()
            .assertUpNextPageDisplayed()
            .assertUpNextEpisodeDisplayed(breakingBadTraktId)

        homeRobot
            .clickDiscoverTab()
            .assertTabSelected(HomeTestTags.DISCOVER_TAB)

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .assertUpNextCardDisplayed(breakingBadTraktId)

        // Continue tracking and mark season watched
        homeRobot
            .clickLibraryTab()
            .assertTabSelected(HomeTestTags.LIBRARY_TAB)

        libraryRobot
            .clickShowRow(breakingBadTraktId)

        showDetailsRobot
            .assertShowDetailsDisplayed()
            .assertStopTrackingButtonDisplayed()
            .assertContinueTrackingSectionDisplayed()
            .clickContinueTrackingMarkWatched(pilotEpisodeTraktId)
            .clickSeasonChip(seasonNumber = 1L)

        seasonDetailsRobot
            .assertSeasonDetailsDisplayed()
            .scrollToMarkUnwatchedButton(pilotEpisodeTraktId)
            .assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
            .clickMarkWatched(secondEpisodeTraktId)
            .scrollToMarkUnwatchedButton(secondEpisodeTraktId)
            .assertMarkUnwatchedDisplayed(secondEpisodeTraktId)
            .pressBack()

        showDetailsRobot
            .pressBack()

        libraryRobot
            .assertLibraryScreenDisplayed()

        // Open Episode Sheet from Discover UpNext card
        homeRobot.clickDiscoverTab()
            .assertTabSelected(HomeTestTags.DISCOVER_TAB)

        discoverRobot
            .assertFeaturedPagerDisplayed()
            .assertUpNextCardDisplayed(breakingBadTraktId)
            .clickUpNextCard(breakingBadTraktId)

        episodeSheetRobot
            .assertEpisodeSheetDisplayed()
            .clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)

        showDetailsRobot
            .assertStopTrackingButtonDisplayed()
            .pressBack()

        // UpNext Flow
        homeRobot.clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertProgressScreenDisplayed()
            .assertUpNextTabSelected()
            .assertLoadingIndicatorDoesNotExist()
            .assertUpNextPageDisplayed()
            .scrollToUpNextEpisode(breakingBadTraktId)
            .assertUpNextEpisodeDisplayed(breakingBadTraktId)
            .clickUpNextEpisodeRow(breakingBadTraktId)

        seasonDetailsRobot
            .assertSeasonDetailsDisplayed()
            .scrollToMarkUnwatchedButton(pilotEpisodeTraktId)
            .assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
            .clickBackButton()

        // Calendar Flow
        progressRobot
            .clickCalendarTab()
            .assertCalendarTabSelected()
            .waitForIdle()

        calendarRobot
            .assertLoadingIndicatorDoesNotExist()
            .assertCalendarScreenDisplayed()
            .assertWeekLabelDisplayed("Apr 19, 2026 - Apr 25, 2026")
            .clickNextWeek()
            .assertWeekLabelDisplayed("Apr 26, 2026 - May 2, 2026")

        progressRobot
            .clickUpNextTab()

        // Verify marked-watched updates propagate to Progress tab
        homeRobot.clickProgressTab()
            .assertTabSelected(HomeTestTags.PROGRESS_TAB)

        progressRobot
            .assertUpNextTabSelected()
            .assertLoadingIndicatorDoesNotExist()
            .assertUpNextPageDisplayed()
            .scrollToUpNextEpisode(breakingBadTraktId)
            .assertUpNextEpisodeDisplayed(breakingBadTraktId)

        // Trigger token refresh round-trip
        scenarios.stubTokenRefresh()

        homeRobot.clickProfileTab()
            .assertTabSelected(HomeTestTags.PROFILE_TAB)

        profileRobot
            .assertUserNameDisplayed()
            .clickSettingsButton()

        settingsRobot
            .assertSettingsScreenDisplayed()
            .scrollToTraktAccountRow()
            .clickTraktAccountRow()
            .assertLogoutDialogDisplayed()
            .clickLogoutConfirm()
            .assertLogoutDialogDoesNotExist()
            .clickBackButton()

        // Verify unauthenticated state
        profileRobot
            .scrollToSignInButton()
            .assertSignInButtonDisplayed()
    }
}
