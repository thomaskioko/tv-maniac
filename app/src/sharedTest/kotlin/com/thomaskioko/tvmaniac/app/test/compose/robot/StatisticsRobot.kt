package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.statistics.StatisticsTestTags

@OptIn(ExperimentalTestApi::class)
internal class StatisticsRobot(composeUi: ComposeUiTest) : BaseRobot<StatisticsRobot>(composeUi) {

    fun assertStatisticsScreenDisplayed() = apply {
        assertDisplayed(StatisticsTestTags.SCREEN_TEST_TAG)
    }

    fun assertEmptyStateDisplayed() = apply {
        assertDisplayed(StatisticsTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun assertContentDisplayed() = apply {
        assertDisplayed(StatisticsTestTags.CONTENT_TEST_TAG)
    }

    fun assertLockedStateDisplayed() = apply {
        assertDisplayed(StatisticsTestTags.LOCKED_STATE_TEST_TAG)
    }

    fun assertTileDisplayed(id: String) = apply {
        assertDisplayed(StatisticsTestTags.tile(id))
    }

    fun clickBackButton() = apply {
        click(StatisticsTestTags.BACK_BUTTON_TEST_TAG)
    }
}
