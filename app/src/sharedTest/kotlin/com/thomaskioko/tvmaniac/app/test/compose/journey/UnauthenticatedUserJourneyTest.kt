package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.settings.presenter.ThemeModel
import com.thomaskioko.tvmaniac.testing.integration.ui.SystemDialog
import com.thomaskioko.tvmaniac.testing.integration.ui.dismissSystemDialog
import org.junit.Before
import org.junit.Test

internal class UnauthenticatedUserJourneyTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val breakingBadTmdbId = 1396L
    private val breakingBadSeasons = listOf(1L, 2L)
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val betterCallSaulTraktId = 59660L

    @Before
    fun stubEndpoints() {
        scenarios.stubUnauthenticatedJourney()
    }

    @Test
    fun givenUnauthenticatedUser_whenNavigatesAllScreens_thenChangesAppearanceAndNotifications() {
        // Verify public content on Discover
        discoverRobot.assertDiscoverScreenDisplayed()
        discoverRobot.assertShowCardDisplayed(breakingBadTraktId)
        discoverRobot.assertUpNextCardDoesNotExist(breakingBadTraktId)

        // Verify featured pager
        discoverRobot.assertFeaturedPagerDisplayed()
        discoverRobot.assertFeaturedShowDisplayed(breakingBadTraktId)
        discoverRobot.swipeFeaturedPagerLeft()
        discoverRobot.assertFeaturedShowDisplayed(betterCallSaulTraktId)
        discoverRobot.swipeFeaturedPagerRight()
        discoverRobot.assertFeaturedShowDisplayed(breakingBadTraktId)

        // Search for show and navigate back
        val query = "Breaking Bad"
        scenarios.search.stubSearch(query)
        discoverRobot.navigateToSearchTab()
        searchRobot.assertSearchScreenDisplayed()
        searchRobot.enterSearchQuery(query)
        searchRobot.assertResultItemDisplayed(breakingBadTraktId)
        searchRobot.clickResultItem(breakingBadTraktId)
        showDetailsRobot.assertTrackButtonDisplayed()
        showDetailsRobot.pressBack()
        searchRobot.assertSearchScreenDisplayed()
        searchRobot.pressBack()

        // Visit show details from Discover
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertTrackButtonDisplayed()
        showDetailsRobot.pressBack()
        discoverRobot.assertDiscoverScreenDisplayed()

        // Verify logged-out empty states for Progress and Calendar
        homeRobot.clickProgressTab()
        progressRobot.assertProgressScreenDisplayed()
        progressRobot.assertUpNextEmptyStateDisplayed()
        progressRobot.assertEpisodeRowDoesNotExist(breakingBadTraktId)

        progressRobot.clickCalendarTab()
        calendarRobot.assertLoggedOutStateDisplayed()
        progressRobot.clickUpNextTab()

        // Verify empty library state
        homeRobot.clickLibraryTab()
        libraryRobot.assertEmptyStateDisplayed()
        libraryRobot.assertShowRowDoesNotExist(breakingBadTraktId)

        // Navigate to Profile and open Settings
        homeRobot.clickProfileTab()
        profileRobot.assertSignInButtonDisplayed()
        profileRobot.clickSettingsButton()
        settingsRobot.assertSettingsScreenDisplayed()

        // Change theme from SYSTEM to DARK
        settingsRobot.scrollToThemeSwatch(ThemeModel.SYSTEM)
        settingsRobot.assertThemeSwatchSelected(ThemeModel.SYSTEM)
        settingsRobot.scrollToThemeSwatch(ThemeModel.DARK)
        settingsRobot.clickThemeSwatch(ThemeModel.DARK)
        settingsRobot.assertThemeSwatchSelected(ThemeModel.DARK)
        settingsRobot.assertThemeSwatchNotSelected(ThemeModel.SYSTEM)

        // Dismiss episode notifications rationale
        settingsRobot.scrollToEpisodeNotificationsToggle()
        settingsRobot.assertEpisodeNotificationsDisabled()
        settingsRobot.clickEpisodeNotificationsToggle()
        rootRobot.assertNotificationRationaleDisplayed()
        rootRobot.dismissNotificationRationale()
        settingsRobot.assertEpisodeNotificationsDisabled()

        // Accept episode notifications rationale
        settingsRobot.clickEpisodeNotificationsToggle()
        rootRobot.assertNotificationRationaleDisplayed()
        rootRobot.acceptNotificationRationale()
        rootRobot.assertNotificationRationaleDoesNotExist()
        composeTestRule.dismissSystemDialog(SystemDialog.NotificationPermissionDeny)

        // Follow show locally
        settingsRobot.pressBack()
        homeRobot.clickDiscoverTab()
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertTrackButtonDisplayed()
        showDetailsRobot.clickTrackButton()
        showDetailsRobot.assertStopTrackingButtonDisplayed()

        // Continue tracking and mark season watched
        showDetailsRobot.clickContinueTrackingMarkWatched(pilotEpisodeTraktId)

        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(secondEpisodeTraktId)

        seasonDetailsRobot.pressBack()
        showDetailsRobot.assertStopTrackingButtonDisplayed()
        showDetailsRobot.pressBack()
        discoverRobot.assertDiscoverScreenDisplayed()

        // Open Episode Sheet from Discover UpNext card
        discoverRobot.assertUpNextCardDisplayed(breakingBadTraktId)
        discoverRobot.clickUpNextCard(breakingBadTraktId)
        episodeSheetRobot.assertEpisodeSheetDisplayed()
        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)
        showDetailsRobot.assertStopTrackingButtonDisplayed()
        showDetailsRobot.pressBack()
        discoverRobot.assertDiscoverScreenDisplayed()

        // Verify offline follow in Library
        homeRobot.clickLibraryTab()
        libraryRobot.assertShowRowDisplayed(breakingBadTraktId)
        libraryRobot.clickShowRow(breakingBadTraktId)
        showDetailsRobot.assertStopTrackingButtonDisplayed()

        // Raise login prompt and confirm login
        scenarios.stubProfileOnSignIn()
        showDetailsRobot.clickAddToListButton()
        showDetailsRobot.assertLoginPromptDisplayed()
        showDetailsRobot.confirmLoginPrompt()
        showDetailsRobot.assertLoginPromptDoesNotExist()
        showDetailsRobot.pressBack()
        homeRobot.clickProfileTab()
        profileRobot.assertUserCardDisplayed(slug = "integration-test-user")
    }

    @Test
    fun givenUnauthenticatedUser_whenSignsInFromProfile_thenSeesUserCard() {
        scenarios.stubProfileOnSignIn()
        homeRobot.clickProfileTab()
        profileRobot.assertSignInButtonDisplayed()
        profileRobot.clickSignInButton()
        profileRobot.assertUserCardDisplayed(slug = "integration-test-user")
    }

    @Test
    fun givenUnauthenticatedUser_whenRationaleDismissedInSettings_thenDoesNotShowAgainOnAuth() {
        // Dismiss rationale from Settings
        homeRobot.clickProfileTab()

        profileRobot.clickSettingsButton()
        settingsRobot.assertSettingsScreenDisplayed()

        settingsRobot.scrollToEpisodeNotificationsToggle()
        settingsRobot.clickEpisodeNotificationsToggle()
        rootRobot.assertNotificationRationaleDisplayed()
        rootRobot.dismissNotificationRationale()
        rootRobot.assertNotificationRationaleDoesNotExist()

        // Login user
        settingsRobot.pressBack()
        profileRobot.assertSignInButtonDisplayed()

        scenarios.stubProfileOnSignIn()

        profileRobot.clickSignInButton()
        profileRobot.assertUserCardDisplayed(slug = "integration-test-user")

        // Verify rationale remains hidden after auth transition
        profileRobot.clickSettingsButton()
        settingsRobot.assertSettingsScreenDisplayed()
        settingsRobot.pressBack()
        profileRobot.assertUserCardDisplayed(slug = "integration-test-user")

        rootRobot.assertNotificationRationaleDoesNotExist()
    }
}
