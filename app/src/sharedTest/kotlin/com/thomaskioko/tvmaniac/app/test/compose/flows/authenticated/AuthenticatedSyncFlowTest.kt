package com.thomaskioko.tvmaniac.app.test.compose.flows.authenticated

import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import org.junit.Before
import org.junit.Test

internal class AuthenticatedSyncFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val breakingBadTmdbId = 1396L
    private val breakingBadSeasons = listOf(1L, 2L)

    @Before
    fun setUp() {
        scenarios.stubAuthenticatedSync(
            traktShowId = breakingBadTraktId,
            tmdbShowId = breakingBadTmdbId,
            seasonNumbers = breakingBadSeasons,
        )
    }

    @Test
    fun shouldRenderWatchlistRowOnLibraryAfterAuthenticatedSync() {
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
    }

    @Test
    fun shouldRenderUpNextEpisodeRowOnProgressAfterAuthenticatedSync() {
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
    }

    @Test
    fun shouldRenderUpNextCardOnDiscoverAfterAuthenticatedSync() {
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
        homeRobot.clickDiscoverTab()
        discoverRobot.verifyUpNextCardIsShown(breakingBadTraktId)
    }

    @Test
    fun shouldRenderProfileUserCardAfterAuthenticatedSync() {
        homeRobot.clickProfileTab()
        profileRobot.verifyUserCardIsShown(TEST_PROFILE_SLUG)
    }

    @Test
    fun shouldOpenSettingsWhenSettingsIconIsTappedOnProfile() {
        homeRobot.clickProfileTab()
        profileRobot.verifyUserCardIsShown(TEST_PROFILE_SLUG)
        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()
    }
}
