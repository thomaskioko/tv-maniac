package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.util.BaseAppRobolectricTest
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags
import org.junit.Before
import kotlin.test.Test

internal class DiscoverToSeasonDetailsFlowTest : BaseAppRobolectricTest() {

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
    fun `should render season chips on show details from real pipeline`() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.verifyShowDetailsIsShown()
        showDetailsRobot.verifySeasonChipIsShown(seasonNumber = 1L)
        showDetailsRobot.verifySeasonChipIsShown(seasonNumber = 2L)
    }

    @Test
    fun `should open season details with episodes when season chip is tapped`() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifySeasonDetailsIsShown()
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun `should open episode sheet when episode row is tapped`() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.clickEpisodeRow(pilotEpisodeTraktId)
        episodeSheetRobot.verifyEpisodeSheetIsShown()
    }

    @Test
    fun `should collapse and expand episode list when header is tapped`() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
        seasonDetailsRobot.clickEpisodeHeader()
        seasonDetailsRobot.verifyEpisodeRowIsHidden(pilotEpisodeTraktId)
        seasonDetailsRobot.clickEpisodeHeader()
        seasonDetailsRobot.verifyEpisodeRowIsShown(pilotEpisodeTraktId)
    }

    @Test
    fun `should pop season details and restore show details on back press`() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.verifySeasonDetailsIsShown()
        seasonDetailsRobot.pressBack()
        seasonDetailsRobot.verifyTagHidden(SeasonDetailsTestTags.SCREEN_TEST_TAG)
        showDetailsRobot.verifyShowDetailsIsShown()
    }

    @Test
    fun `should toggle episode watched state when watched button is tapped`() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.clickMarkWatched(pilotEpisodeTraktId)
        seasonDetailsRobot.verifyMarkUnwatchedIsShown(pilotEpisodeTraktId)
    }
}
