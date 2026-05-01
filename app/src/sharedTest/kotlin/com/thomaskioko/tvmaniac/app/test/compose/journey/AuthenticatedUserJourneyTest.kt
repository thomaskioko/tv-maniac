package com.thomaskioko.tvmaniac.app.test.compose.journey

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import org.junit.Before
import org.junit.Test

internal class AuthenticatedUserJourneyTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val betterCallSaulTraktId = 59660L

    @Before
    fun stubInitialState() {
        scenarios.stubUnauthenticatedState()
    }

    @Test
    fun authenticatedUserSignsInExploresSyncedSurfacesAndSignsOut() {
        // Verify public content on Discover
        discoverRobot.verifyDiscoverScreenIsShown()
        discoverRobot.verifyFeaturedShowIsShown(breakingBadTraktId)
        discoverRobot.verifyUpNextCardIsHidden(breakingBadTraktId)

        // Verify featured pager
        discoverRobot.verifyFeaturedPagerIsShown()
        discoverRobot.verifyFeaturedShowIsShown(breakingBadTraktId)
        discoverRobot.swipeFeaturedPagerLeft()
        discoverRobot.verifyFeaturedShowIsShown(betterCallSaulTraktId)
        discoverRobot.swipeFeaturedPagerRight()
        discoverRobot.verifyFeaturedShowIsShown(breakingBadTraktId)

        // Navigate to Profile and verify Sign In CTA
        homeRobot.clickProfileTab()
        profileRobot.verifySignInButtonIsShown()

        // Login user
        scenarios.stubAuthenticatedSyncOnSignIn()

        profileRobot.clickSignInButton()

        rootRobot.verifyNotificationRationaleIsShownAndDismissed()

        profileRobot.verifyUserCardIsShown(slug = TEST_PROFILE_SLUG)

        // Verify synced surfaces appear after auth
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)

        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)

        homeRobot.clickDiscoverTab()
        discoverRobot.verifyUpNextCardIsShown(breakingBadTraktId)

        // Continue tracking and mark season watched
        homeRobot.clickLibraryTab()
        libraryRobot.clickShowRow(breakingBadTraktId)
        showDetailsRobot.verifyStopTrackingButtonIsShown()

        showDetailsRobot.clickContinueTrackingMarkWatched(pilotEpisodeTraktId)

        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifySeasonDetailsIsShown()
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(pilotEpisodeTraktId)
        seasonDetailsRobot.clickMarkWatched(secondEpisodeTraktId)
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(secondEpisodeTraktId)

        seasonDetailsRobot.pressBack()
        showDetailsRobot.verifyStopTrackingButtonIsShown()
        showDetailsRobot.pressBack()
        libraryRobot.verifyLibraryScreenIsShown()

        // Open Episode Sheet from Discover UpNext card
        homeRobot.clickDiscoverTab()
        discoverRobot.verifyUpNextCardIsShown(breakingBadTraktId)
        discoverRobot.clickUpNextCard(breakingBadTraktId)
        episodeSheetRobot.verifyEpisodeSheetIsShown()
        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)
        showDetailsRobot.verifyStopTrackingButtonIsShown()
        showDetailsRobot.pressBack()

        // Verify marked-watched updates propagate to Progress tab
        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)

        // Trigger token refresh round-trip
        scenarios.stubTokenRefresh()

        homeRobot.clickProfileTab()
        profileRobot.verifyUserCardIsShown(slug = TEST_PROFILE_SLUG)

        // Logout via Settings
        homeRobot.clickProfileTab()
        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()
        settingsRobot.scrollToTraktAccountRow()
        settingsRobot.clickTraktAccountRow()
        settingsRobot.verifyLogoutDialogIsShown()
        settingsRobot.clickLogoutConfirm()
        settingsRobot.verifyLogoutDialogIsHidden()

        // Verify unauthenticated state
        settingsRobot.pressBack()
        profileRobot.verifySignInButtonIsShown()
    }
}
