package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.home.nav.di.model.HomeConfig
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags

@OptIn(ExperimentalTestApi::class)
internal class HomeRobot(composeUi: ComposeUiTest) : BaseRobot(composeUi) {

    fun assertTabSelected(config: HomeConfig) {
        assertSelected(tag = config.testTag())
    }

    fun assertTabNotSelected(config: HomeConfig) {
        assertNotSelected(tag = config.testTag())
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
