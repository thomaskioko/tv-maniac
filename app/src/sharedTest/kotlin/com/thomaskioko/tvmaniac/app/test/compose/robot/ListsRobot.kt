package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.lists.ListsTestTags

@OptIn(ExperimentalTestApi::class)
internal class ListsRobot(composeUi: ComposeUiTest) : BaseRobot<ListsRobot>(composeUi) {

    fun assertListsScreenDisplayed() = apply {
        assertDisplayed(ListsTestTags.SCREEN_TEST_TAG)
    }

    fun assertListCardDisplayed(id: Long) = apply {
        assertDisplayed(ListsTestTags.listCard(id))
    }

    fun clickBackButton() = apply {
        click(ListsTestTags.BACK_BUTTON_TEST_TAG)
    }
}
