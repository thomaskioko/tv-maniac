package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.episodesheet.EpisodeSheetTestTags

internal class EpisodeSheetRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertEpisodeSheetDisplayed() {
        assertDisplayed(EpisodeSheetTestTags.SHEET_TEST_TAG)
    }

    fun assertActionItemDisplayed(action: EpisodeSheetActionItem) {
        assertExists(EpisodeSheetTestTags.actionItem(action.name))
    }

    fun clickActionItem(action: EpisodeSheetActionItem) {
        click(EpisodeSheetTestTags.actionItem(action.name), useSemanticsAction = true)
    }
}
