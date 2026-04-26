package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.util.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags
import org.junit.Before
import org.junit.Test

internal class DiscoverToSeasonDetailsFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val breakingBadTmdbId = 1396L
    private val pilotEpisodeTraktId = 73640L

    @Before
    fun stubEndpoints() {
        scenarios.stubShowDetailsNavigation(
            traktShowId = breakingBadTraktId,
            tmdbShowId = breakingBadTmdbId,
            seasonNumbers = listOf(1L, 2L),
        )
    }

    @Test
    fun shouldRenderSeasonChipsOnShowDetailsFromRealPipeline() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyShowDetailsIsShown()
        showDetailsRobot.verifySeasonChipIsShown(seasonNumber = 1L)
        showDetailsRobot.verifySeasonChipIsShown(seasonNumber = 2L)
    }

    @Test
    fun shouldOpenSeasonDetailsWithEpisodesWhenSeasonChipIsTapped() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifySeasonDetailsIsShown()
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun shouldOpenEpisodeSheetWhenEpisodeRowIsTapped() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.clickEpisodeRow(pilotEpisodeTraktId)
        episodeSheetRobot.verifyEpisodeSheetIsShown()
    }

    @Test
    fun shouldCollapseAndExpandEpisodeListWhenHeaderIsTapped() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
        seasonDetailsRobot.clickEpisodeHeader()
        seasonDetailsRobot.verifyEpisodeRowIsHidden(pilotEpisodeTraktId)
        seasonDetailsRobot.clickEpisodeHeader()
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun shouldPopSeasonDetailsAndRestoreShowDetailsOnBackPress() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifySeasonDetailsIsShown()
        seasonDetailsRobot.pressBack()
        seasonDetailsRobot.verifyTagHidden(SeasonDetailsTestTags.SCREEN_TEST_TAG)
        showDetailsRobot.verifyShowDetailsIsShown()
    }

    @Test
    fun shouldToggleEpisodeWatchedStateWhenWatchedButtonIsTapped() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.clickMarkWatched(pilotEpisodeTraktId)
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(pilotEpisodeTraktId)
    }
}
