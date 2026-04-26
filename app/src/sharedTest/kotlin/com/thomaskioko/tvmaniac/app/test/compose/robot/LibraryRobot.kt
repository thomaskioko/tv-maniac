package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.robot.BaseRobot
import com.thomaskioko.tvmaniac.testtags.library.LibraryTestTags

internal class LibraryRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifyLibraryScreenIsShown() {
        verifyTagShown(LibraryTestTags.SCREEN_TEST_TAG)
    }

    fun verifyEmptyStateIsShown() {
        verifyTagShown(LibraryTestTags.EMPTY_STATE_TEST_TAG)
    }

    fun verifyShowRowIsShown(traktId: Long) {
        verifyTagExists(LibraryTestTags.showRow(traktId))
    }

    fun verifyShowRowIsHidden(traktId: Long) {
        verifyTagHidden(LibraryTestTags.showRow(traktId))
    }
}
