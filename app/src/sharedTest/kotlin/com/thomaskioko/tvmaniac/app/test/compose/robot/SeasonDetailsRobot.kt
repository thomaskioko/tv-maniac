package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.robot.BaseRobot
import com.thomaskioko.tvmaniac.testtags.seasondetails.SeasonDetailsTestTags

internal class SeasonDetailsRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifySeasonDetailsIsShown() {
        verifyTagShown(SeasonDetailsTestTags.SCREEN_TEST_TAG)
    }

    fun verifyEpisodeRowIsShown(episodeId: Long) {
        verifyTagExists(SeasonDetailsTestTags.episodeRow(episodeId))
    }

    fun verifyEpisodeRowIsHidden(episodeId: Long) {
        verifyTagHidden(SeasonDetailsTestTags.episodeRow(episodeId))
    }

    fun clickEpisodeHeader() {
        click(SeasonDetailsTestTags.EPISODE_HEADER_TEST_TAG, useSemanticsAction = true)
    }

    fun clickEpisodeRow(episodeId: Long) {
        click(SeasonDetailsTestTags.episodeRow(episodeId), useSemanticsAction = true)
    }

    fun clickMarkWatched(episodeId: Long) {
        click(SeasonDetailsTestTags.markEpisodeWatchedButton(episodeId), useSemanticsAction = true)
    }

    fun verifyMarkUnwatchedIsShown(episodeId: Long) {
        verifyTagExists(SeasonDetailsTestTags.markEpisodeUnwatchedButton(episodeId))
    }
}
