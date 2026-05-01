package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import org.junit.Test

internal class AuthenticatedUserJourneyTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val breakingBadTmdbId = 1396L
    private val breakingBadSeasons = listOf(1L, 2L)
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val betterCallSaulTraktId = 59660L

    @Test
    fun givenAuthenticatedUser_whenSignsIn_thenExploresSyncedSurfacesAndSignsOut() = runAppFlowTest {
        scenarios.stubUnauthenticatedState()

        // Verify public content on Discover
        discoverRobot.assertDiscoverScreenDisplayed()
        discoverRobot.assertFeaturedShowDisplayed(breakingBadTraktId)
        discoverRobot.assertUpNextCardDoesNotExist(breakingBadTraktId)

        // Verify featured pager
        discoverRobot.assertFeaturedPagerDisplayed()
        discoverRobot.assertFeaturedShowDisplayed(breakingBadTraktId)
        discoverRobot.swipeFeaturedPagerLeft()
        discoverRobot.assertFeaturedShowDisplayed(betterCallSaulTraktId)
        discoverRobot.swipeFeaturedPagerRight()
        discoverRobot.assertFeaturedShowDisplayed(breakingBadTraktId)

        // Navigate to Profile and verify Sign In CTA
        homeRobot.clickProfileTab()
        profileRobot.assertSignInButtonDisplayed()

        // Login user
        scenarios.stubAuthenticatedSyncOnSignIn()

        profileRobot.clickSignInButton()

        rootRobot.dismissNotificationRationale()

        profileRobot.assertUserCardDisplayed(slug = TEST_PROFILE_SLUG)

        // Verify synced surfaces appear after auth
        homeRobot.clickLibraryTab()
        libraryRobot.assertShowRowDisplayed(breakingBadTraktId)

        homeRobot.clickProgressTab()
        progressRobot.assertEpisodeRowDisplayed(breakingBadTraktId)

        homeRobot.clickDiscoverTab()
        discoverRobot.assertUpNextCardDisplayed(breakingBadTraktId)

        // Continue tracking and mark season watched
        homeRobot.clickLibraryTab()
        libraryRobot.clickShowRow(breakingBadTraktId)
        showDetailsRobot.assertStopTrackingButtonDisplayed()

        showDetailsRobot.clickContinueTrackingMarkWatched(pilotEpisodeTraktId)

        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.assertMarkUnwatchedDisplayed(secondEpisodeTraktId)

        seasonDetailsRobot.pressBack()
        showDetailsRobot.assertStopTrackingButtonDisplayed()
        showDetailsRobot.pressBack()
        libraryRobot.assertLibraryScreenDisplayed()

        // Open Episode Sheet from Discover UpNext card
        homeRobot.clickDiscoverTab()
        discoverRobot.assertUpNextCardDisplayed(breakingBadTraktId)
        discoverRobot.clickUpNextCard(breakingBadTraktId)
        episodeSheetRobot.assertEpisodeSheetDisplayed()
        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)
        showDetailsRobot.assertStopTrackingButtonDisplayed()
        showDetailsRobot.pressBack()

        // Verify marked-watched updates propagate to Progress tab
        homeRobot.clickProgressTab()
        progressRobot.assertEpisodeRowDisplayed(breakingBadTraktId)

        // Trigger token refresh round-trip
        scenarios.stubTokenRefresh()

        homeRobot.clickProfileTab()
        profileRobot.assertUserCardDisplayed(slug = TEST_PROFILE_SLUG)

        // Logout via Settings
        homeRobot.clickProfileTab()
        profileRobot.clickSettingsButton()
        settingsRobot.assertSettingsScreenDisplayed()
        settingsRobot.scrollToTraktAccountRow()
        settingsRobot.clickTraktAccountRow()
        settingsRobot.assertLogoutDialogDisplayed()
        settingsRobot.clickLogoutConfirm()
        settingsRobot.assertLogoutDialogDoesNotExist()

        // Verify unauthenticated state
        settingsRobot.pressBack()
        profileRobot.assertSignInButtonDisplayed()
    }
}
