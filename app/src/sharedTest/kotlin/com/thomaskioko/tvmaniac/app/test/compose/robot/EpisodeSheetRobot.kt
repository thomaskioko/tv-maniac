package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.hasTestTag
import com.thomaskioko.tvmaniac.presentation.episodedetail.EpisodeSheetActionItem
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.episodesheet.EpisodeSheetTestTags

@OptIn(ExperimentalTestApi::class)
internal class EpisodeSheetRobot(composeUi: ComposeUiTest) : BaseRobot<EpisodeSheetRobot>(composeUi) {

    fun assertEpisodeSheetDisplayed() = apply {
        awaitTagOnce(EpisodeSheetTestTags.SHEET_TEST_TAG, timeoutMillis = SHEET_APPEARANCE_TIMEOUT_MILLIS)
        assertDisplayed(EpisodeSheetTestTags.SHEET_TEST_TAG)
        awaitTagOnce(EpisodeSheetTestTags.TITLE_TEST_TAG)
        awaitMatcherAtLeastOne(matcher = hasTestTag(EpisodeSheetTestTags.actionItem(EpisodeSheetActionItem.TOGGLE_WATCHED.name)))
        waitForIdle()
    }

    fun assertActionItemDisplayed(action: EpisodeSheetActionItem) = apply {
        val tag = EpisodeSheetTestTags.actionItem(action.name)
        scrollTo(tag)
        assertDisplayed(tag)
    }

    fun clickActionItem(action: EpisodeSheetActionItem) = apply {
        val tag = EpisodeSheetTestTags.actionItem(action.name)
        scrollTo(tag)
        click(tag)
    }

    private companion object {
        private const val SHEET_APPEARANCE_TIMEOUT_MILLIS: Long = 15_000
    }
}
