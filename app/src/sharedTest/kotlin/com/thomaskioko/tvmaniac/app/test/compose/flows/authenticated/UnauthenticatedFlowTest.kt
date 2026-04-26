package com.thomaskioko.tvmaniac.app.test.compose.flows.authenticated

import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import org.junit.Before
import org.junit.Test

internal class UnauthenticatedFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L

    @Before
    fun setUp() {
        scenarios.stubUnauthenticatedState()
    }

    @Test
    fun shouldRenderDiscoverPublicContentWithoutUpNextCardWhenLoggedOut() {
        discoverRobot.verifyDiscoverScreenIsShown()
        discoverRobot.verifyShowCardIsShown(breakingBadTraktId)
        discoverRobot.verifyUpNextCardIsHidden(breakingBadTraktId)
    }

    @Test
    fun shouldRenderEmptyStateOnLibraryWhenLoggedOut() {
        homeRobot.clickLibraryTab()
        libraryRobot.verifyEmptyStateIsShown()
        libraryRobot.verifyShowRowIsHidden(breakingBadTraktId)
    }

    @Test
    fun shouldRenderEmptyStateOnProgressUpNextWhenLoggedOut() {
        homeRobot.clickProgressTab()
        progressRobot.verifyUpNextEmptyStateIsShown()
        progressRobot.verifyEpisodeRowIsHidden(breakingBadTraktId)
    }

    @Test
    fun shouldRenderSignInButtonOnProfileWhenLoggedOut() {
        homeRobot.clickProfileTab()
        profileRobot.verifySignInButtonIsShown()
    }

    @Test
    fun shouldOpenSettingsWhenSettingsIconIsTappedOnProfileWhenLoggedOut() {
        homeRobot.clickProfileTab()
        profileRobot.verifySignInButtonIsShown()
        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()
    }
}
