package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testing.integration.ui.isNotSelected
import com.thomaskioko.tvmaniac.testing.integration.ui.isSelected
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags

internal class HomeRobot(composeTestRule: ComposeContentTestRule) : BaseRobot(composeTestRule) {

    fun assertTabSelected(config: HomeConfig) {
        composeTestRule.isSelected(config.testTag())
    }

    fun assertTabNotSelected(config: HomeConfig) {
        composeTestRule.isNotSelected(config.testTag())
    }

    fun clickDiscoverTab() {
        click(HomeTestTags.DISCOVER_TAB, useSemanticsAction = true)
        assertTabSelected(HomeConfig.Discover)
    }

    fun clickProgressTab() {
        click(HomeTestTags.PROGRESS_TAB, useSemanticsAction = true)
        assertTabSelected(HomeConfig.Progress)
    }

    fun clickLibraryTab() {
        click(HomeTestTags.LIBRARY_TAB, useSemanticsAction = true)
        assertTabSelected(HomeConfig.Library)
    }

    fun clickProfileTab() {
        click(HomeTestTags.PROFILE_TAB, useSemanticsAction = true)
        assertTabSelected(HomeConfig.Profile)
    }

    private fun HomeConfig.testTag(): String = when (this) {
        HomeConfig.Discover -> HomeTestTags.DISCOVER_TAB
        HomeConfig.Progress -> HomeTestTags.PROGRESS_TAB
        HomeConfig.Library -> HomeTestTags.LIBRARY_TAB
        HomeConfig.Profile -> HomeTestTags.PROFILE_TAB
    }
}
