package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import com.thomaskioko.tvmaniac.testing.integration.ui.robot.BaseRobot
import com.thomaskioko.tvmaniac.testing.integration.ui.util.isNotSelected
import com.thomaskioko.tvmaniac.testing.integration.ui.util.isSelected
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags

internal class HomeRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun verifyTabSelected(config: HomeConfig) {
        composeTestRule.isSelected(config.testTag())
    }

    fun verifyTabNotSelected(config: HomeConfig) {
        composeTestRule.isNotSelected(config.testTag())
    }

    fun clickDiscoverTab(): DiscoverRobot {
        click(HomeTestTags.DISCOVER_TAB, useSemanticsAction = true)
        return DiscoverRobot(composeTestRule)
    }

    fun clickProgressTab() {
        click(HomeTestTags.PROGRESS_TAB, useSemanticsAction = true)
    }

    fun clickLibraryTab() {
        click(HomeTestTags.LIBRARY_TAB, useSemanticsAction = true)
    }

    fun clickProfileTab() {
        click(HomeTestTags.PROFILE_TAB, useSemanticsAction = true)
    }

    private fun HomeConfig.testTag(): String = when (this) {
        HomeConfig.Discover -> HomeTestTags.DISCOVER_TAB
        HomeConfig.Progress -> HomeTestTags.PROGRESS_TAB
        HomeConfig.Library -> HomeTestTags.LIBRARY_TAB
        HomeConfig.Profile -> HomeTestTags.PROFILE_TAB
    }
}
