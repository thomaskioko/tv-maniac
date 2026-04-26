package com.thomaskioko.tvmaniac.app.test.compose.flows.sheet

import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import org.junit.Before
import kotlin.test.Test

internal class EpisodeSheetFlowTest : BaseAppRobolectricTest() {

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
    fun `should open episode sheet from discover up next card`() {
        // 1. Navigate to Library to trigger sync
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        // 2. Navigate to Progress to trigger UpNext sync
        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
        // 3. Navigate back to Discover
        homeRobot.clickDiscoverTab()
        // 4. Click on UpNext card
        discoverRobot.clickUpNextCard(breakingBadTraktId)
        // 5. Verify Episode Sheet is visible
        episodeSheetRobot.verifyEpisodeSheetIsShown()
    }

    @Test
    fun `should open show details when sheet open-show action is tapped`() {
        // 1. Trigger sync chain (Library -> Progress)
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
        // 2. Navigate back to Discover and open Episode Sheet
        homeRobot.clickDiscoverTab()
        discoverRobot.clickUpNextCard(breakingBadTraktId)
        // 3. Verify Open Show action exists and click it
        episodeSheetRobot.verifyActionItemIsShown(EpisodeSheetActionItem.OPEN_SHOW)
        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)
        // 4. Verify navigation to Show Details (Stop Tracking button present)
        showDetailsRobot.verifyStopTrackingButtonIsShown()
    }
}
