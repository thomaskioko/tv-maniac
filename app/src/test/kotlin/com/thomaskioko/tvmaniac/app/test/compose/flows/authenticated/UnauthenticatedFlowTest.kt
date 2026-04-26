package com.thomaskioko.tvmaniac.app.test.compose.flows.authenticated

import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import org.junit.Before
import kotlin.test.Test

internal class UnauthenticatedFlowTest : BaseAppRobolectricTest() {

    private val breakingBadTraktId = 1388L

    @Before
    fun setUp() {
        scenarios.stubUnauthenticatedState()
    }

    @Test
    fun `should render discover public content without up next card when logged out`() {
        discoverRobot.verifyDiscoverScreenIsShown()
        discoverRobot.verifyShowCardIsShown(breakingBadTraktId)
        discoverRobot.verifyUpNextCardIsHidden(breakingBadTraktId)
    }

    @Test
    fun `should render empty state on library when logged out`() {
        homeRobot.clickLibraryTab()
        libraryRobot.verifyEmptyStateIsShown()
        libraryRobot.verifyShowRowIsHidden(breakingBadTraktId)
    }

    @Test
    fun `should render empty state on progress up next when logged out`() {
        homeRobot.clickProgressTab()
        progressRobot.verifyUpNextEmptyStateIsShown()
        progressRobot.verifyEpisodeRowIsHidden(breakingBadTraktId)
    }

    @Test
    fun `should render sign in button on profile when logged out`() {
        homeRobot.clickProfileTab()
        profileRobot.verifySignInButtonIsShown()
    }

    @Test
    fun `should open settings when settings icon is tapped on profile when logged out`() {
        homeRobot.clickProfileTab()
        profileRobot.verifySignInButtonIsShown()
        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()
    }
}
