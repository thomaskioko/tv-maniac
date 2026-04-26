package com.thomaskioko.tvmaniac.app.test.compose.flows.sheet

import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import org.junit.Before
import org.junit.Test

internal class EpisodeSheetFlowTest : BaseAppFlowTest() {

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
    fun shouldOpenEpisodeSheetFromDiscoverUpNextCard() {
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
        homeRobot.clickDiscoverTab()
        discoverRobot.clickUpNextCard(breakingBadTraktId)
        episodeSheetRobot.verifyEpisodeSheetIsShown()
    }

    @Test
    fun shouldOpenShowDetailsWhenSheetOpenShowActionIsTapped() {
        homeRobot.clickLibraryTab()
        libraryRobot.verifyShowRowIsShown(breakingBadTraktId)
        homeRobot.clickProgressTab()
        progressRobot.verifyEpisodeRowIsShown(breakingBadTraktId)
        homeRobot.clickDiscoverTab()
        discoverRobot.clickUpNextCard(breakingBadTraktId)
        episodeSheetRobot.verifyActionItemIsShown(EpisodeSheetActionItem.OPEN_SHOW)
        episodeSheetRobot.clickActionItem(EpisodeSheetActionItem.OPEN_SHOW)
        showDetailsRobot.verifyStopTrackingButtonIsShown()
    }
}
