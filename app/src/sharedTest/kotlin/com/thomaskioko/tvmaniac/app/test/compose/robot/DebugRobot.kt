package com.thomaskioko.tvmaniac.app.test.compose.robot

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import com.thomaskioko.tvmaniac.subscription.api.AccountType
import com.thomaskioko.tvmaniac.testing.integration.ui.BaseRobot
import com.thomaskioko.tvmaniac.testtags.debug.DebugTestTags

@OptIn(ExperimentalTestApi::class)
internal class DebugRobot(composeUi: ComposeUiTest) : BaseRobot<DebugRobot>(composeUi) {

    fun assertDebugMenuScreenDisplayed() = apply {
        assertDisplayed(DebugTestTags.SCREEN_TEST_TAG)
    }

    fun scrollToAccountTypeRow() = apply {
        scrollToListTag(DebugTestTags.LIST_TEST_TAG, DebugTestTags.ACCOUNT_TYPE_ROW_TEST_TAG)
    }

    fun clickAccountTypeRow() = apply {
        click(DebugTestTags.ACCOUNT_TYPE_ROW_TEST_TAG)
    }

    fun assertAccountTypeDialogDisplayed() = apply {
        assertExists(DebugTestTags.ACCOUNT_TYPE_DIALOG_TEST_TAG)
    }

    fun selectAccountType(override: AccountType) = apply {
        click(DebugTestTags.accountTypeOption(override.name))
    }

    fun assertAccountTypeOptionSelected(override: AccountType) = apply {
        assertSelected(DebugTestTags.accountTypeOption(override.name))
    }

    fun assertAccountTypeOptionNotSelected(override: AccountType) = apply {
        assertNotSelected(DebugTestTags.accountTypeOption(override.name))
    }

    fun assertAccountTypeRowSubtitle(text: String) = apply {
        assertTextContains(DebugTestTags.ACCOUNT_TYPE_ROW_TEST_TAG, text)
    }
}
