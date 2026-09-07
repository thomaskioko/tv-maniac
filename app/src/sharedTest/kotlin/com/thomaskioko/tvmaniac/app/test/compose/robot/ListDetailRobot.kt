package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.lists.ListDetailTestTags

@OptIn(ExperimentalTestApi::class)
internal class ListDetailRobot(composeUi: ComposeUiTest) : BaseRobot<ListDetailRobot>(composeUi) {

    fun assertListDetailScreenDisplayed() = apply {
        assertDisplayed(ListDetailTestTags.SCREEN_TEST_TAG)
    }

    fun assertShowCardDisplayed(tmdbId: Long) = apply {
        assertDisplayed(ListDetailTestTags.showCard(tmdbId))
    }

    fun assertShowCardDoesNotExist(tmdbId: Long) = apply {
        assertDoesNotExist(ListDetailTestTags.showCard(tmdbId))
    }

    fun longClickShowCard(tmdbId: Long) = apply {
        longClickWithTouch(ListDetailTestTags.showCard(tmdbId))
    }

    fun assertRemoveConfirmationDisplayed() = apply {
        assertDisplayed(ListDetailTestTags.REMOVE_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickRemoveConfirm() = apply {
        click(ListDetailTestTags.REMOVE_CONFIRM_BUTTON_TEST_TAG)
    }

    fun clickRemoveCancel() = apply {
        click(ListDetailTestTags.REMOVE_CANCEL_BUTTON_TEST_TAG)
    }

    fun clickBackButton() = apply {
        click(ListDetailTestTags.BACK_BUTTON_TEST_TAG)
    }

    fun clickMoreButton() = apply {
        click(ListDetailTestTags.MORE_BUTTON_TEST_TAG)
    }

    fun clickRename() = apply {
        click(ListDetailTestTags.RENAME_MENU_ITEM_TEST_TAG, useUnmergedTree = true)
    }

    fun clickDeleteList() = apply {
        click(ListDetailTestTags.DELETE_MENU_ITEM_TEST_TAG, useUnmergedTree = true)
    }

    fun enterListName(name: String) = apply {
        replaceText(ListDetailTestTags.RENAME_FIELD_TEST_TAG, name, useUnmergedTree = true)
    }

    fun clickSave() = apply {
        click(ListDetailTestTags.RENAME_SAVE_BUTTON_TEST_TAG, useUnmergedTree = true)
    }

    fun clickConfirmDelete() = apply {
        click(ListDetailTestTags.DELETE_CONFIRM_BUTTON_TEST_TAG, useUnmergedTree = true)
    }

    fun verifyTitle(title: String) = apply {
        assertTextDisplayed(title, substring = false)
    }
}
