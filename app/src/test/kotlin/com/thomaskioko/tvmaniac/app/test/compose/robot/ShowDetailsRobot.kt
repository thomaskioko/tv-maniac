package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.robot.BaseRobot
import com.thomaskioko.tvmaniac.testtags.showdetails.ShowDetailsTestTags

internal class ShowDetailsRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifyShowDetailsIsShown() {
        verifyTagShown(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun verifyTrackButtonIsShown() {
        verifyTagShown(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun verifyStopTrackingButtonIsShown() {
        verifyTagShown(ShowDetailsTestTags.STOP_TRACKING_BUTTON_TEST_TAG)
    }

    fun verifySeasonChipIsShown(seasonNumber: Long) {
        verifyTagExists(ShowDetailsTestTags.seasonChip(seasonNumber))
    }

    fun clickTrackButton() {
        click(ShowDetailsTestTags.TRACK_BUTTON_TEST_TAG)
    }

    fun clickSeasonChip(seasonNumber: Long): SeasonDetailsRobot {
        click(ShowDetailsTestTags.seasonChip(seasonNumber), useSemanticsAction = true)
        return SeasonDetailsRobot(composeTestRule)
    }
}
