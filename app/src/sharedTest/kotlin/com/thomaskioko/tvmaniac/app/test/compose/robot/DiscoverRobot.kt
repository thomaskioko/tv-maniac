package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.testing.integration.ui.robot.BaseRobot
import com.thomaskioko.tvmaniac.testtags.discover.DiscoverTestTags

internal class DiscoverRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifyDiscoverScreenIsShown() {
        verifyTagShown(DiscoverTestTags.SCREEN_TEST_TAG)
    }

    fun verifyShowCardIsShown(traktId: Long) {
        verifyTagShown(DiscoverTestTags.showCard(traktId))
    }

    fun verifyUpNextCardIsShown(traktId: Long) {
        verifyTagShown(DiscoverTestTags.upNextCard(traktId))
    }

    fun verifyUpNextCardIsHidden(traktId: Long) {
        verifyTagHidden(DiscoverTestTags.upNextCard(traktId))
    }

    fun clickShowCard(traktId: Long) {
        click(DiscoverTestTags.showCard(traktId))
    }

    fun clickSearchButton(): SearchRobot {
        click(DiscoverTestTags.SEARCH_BUTTON_TEST_TAG, useSemanticsAction = true)
        return SearchRobot(composeTestRule)
    }

    fun clickUpNextCard(traktId: Long) {
        click(DiscoverTestTags.upNextCard(traktId), useSemanticsAction = true)
    }
}
