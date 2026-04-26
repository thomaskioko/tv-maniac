package com.thomaskioko.tvmaniac.app.test.compose.flows.authenticated

import com.thomaskioko.tvmaniac.app.test.compose.stubs.TEST_PROFILE_SLUG
import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import org.junit.Before
import kotlin.test.Test

internal class AuthenticatedSyncFlowTest : BaseAppRobolectricTest() {

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
    fun `should render watchlist row on library after authenticated sync`() {
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
    }

    @Test
    fun `should render up next episode row on progress after authenticated sync`() {
        // 1. Navigate to Library to trigger sync
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        // 2. Navigate to Progress tab
        homeRobot.clickProgressTab()
        // 3. Verify episode row from synced UpNext exists
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
    }

    @Test
    fun `should render up next card on discover after authenticated sync`() {
        // 1. Navigate to Library to trigger sync
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        // 2. Navigate to Progress to trigger UpNext sync
        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
        // 3. Navigate back to Discover
        homeRobot.clickDiscoverTab()
        // 4. Verify UpNext card is now visible on Discover
        discoverRobot.verifyUpNextCardIsShown(breakingBadTraktId)
    }

    @Test
    fun `should render profile user card after authenticated sync`() {
        homeRobot.clickProfileTab()
        profileRobot.verifyUserCardIsShown(TEST_PROFILE_SLUG)
    }

    @Test
    fun `should open settings when settings icon is tapped on profile`() {
        homeRobot.clickProfileTab()
        profileRobot.verifyUserCardIsShown(TEST_PROFILE_SLUG)
        profileRobot.clickSettingsButton()
        settingsRobot.verifySettingsScreenIsShown()
    }
}
