package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags

@OptIn(ExperimentalTestApi::class)
internal class HomeRobot(composeUi: ComposeUiTest) : BaseRobot<HomeRobot>(composeUi) {

    fun assertTabSelected(tag: String) = apply {
        assertSelected(tag = tag)
    }

    fun assertTabNotSelected(tag: String) = apply {
        assertNotSelected(tag = tag)
    }

    fun clickDiscoverTab() = apply {
        click(HomeTestTags.DISCOVER_TAB)
    }

    fun clickProgressTab() = apply {
        click(HomeTestTags.PROGRESS_TAB)
    }

    fun clickLibraryTab() = apply {
        click(HomeTestTags.LIBRARY_TAB)
    }

    fun clickProfileTab() = apply {
        click(HomeTestTags.PROFILE_TAB)
    }
}
