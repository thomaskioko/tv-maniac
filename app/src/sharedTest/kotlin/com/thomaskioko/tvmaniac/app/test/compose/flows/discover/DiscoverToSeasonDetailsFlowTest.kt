package com.thomaskioko.tvmaniac.app.test.compose.flows.discover

import com.thomaskioko.tvmaniac.app.test.BaseAppFlowTest
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags
import org.junit.Before
import org.junit.Test

internal class DiscoverToSeasonDetailsFlowTest : BaseAppFlowTest() {

    private val breakingBadTraktId = 1388L
    private val pilotEpisodeTraktId = 73640L
    private val secondEpisodeTraktId = 73641L
    private val seasonTwoFirstEpisodeTraktId = 73489L

    @Before
    fun stubEndpoints() {
        scenarios.discover.stubBrowseGraph()
    }

    @Test
    fun givenShowDetails_whenOpened_thenSeasonChipsAreRendered() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.assertShowDetailsDisplayed()
        showDetailsRobot.assertSeasonChipDisplayed(seasonNumber = 1L)
        showDetailsRobot.assertSeasonChipDisplayed(seasonNumber = 2L)
    }

    @Test
    fun givenShowDetails_whenSeasonChipClicked_thenOpensSeasonDetails() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenSeasonDetails_whenEpisodeRowClicked_thenOpensEpisodeSheet() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.clickEpisodeRow(pilotEpisodeTraktId)
        episodeSheetRobot.assertEpisodeSheetDisplayed()
    }

    @Test
    fun givenSeasonDetails_whenEpisodeHeaderClicked_thenTogglesEpisodeList() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)
        seasonDetailsRobot.clickEpisodeHeader()
        seasonDetailsRobot.assertEpisodeRowDoesNotExist(pilotEpisodeTraktId)
        seasonDetailsRobot.clickEpisodeHeader()
        seasonDetailsRobot.assertEpisodeRowDisplayed(pilotEpisodeTraktId)
    }

    @Test
    fun givenSeasonDetails_whenBackIsPressed_thenRestoresShowDetails() {
        discoverRobot.clickShowCard(breakingBadTraktId)
        showDetailsRobot.clickSeasonChip(seasonNumber = 1L)
        seasonDetailsRobot.assertSeasonDetailsDisplayed()
        seasonDetailsRobot.pressBack()
        seasonDetailsRobot.assertDoesNotExist(SeasonDetailsTestTags.SCREEN_TEST_TAG)
        showDetailsRobot.assertShowDetailsDisplayed()
    }
}
