package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.episodesheet.EpisodeSheetTestTags

@OptIn(ExperimentalTestApi::class)
internal class EpisodeSheetRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

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
