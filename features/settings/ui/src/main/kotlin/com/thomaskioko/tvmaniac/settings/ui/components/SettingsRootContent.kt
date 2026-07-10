package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.settings.presenter.OpenSettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPage
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroupDivider
import com.thomaskioko.tvmaniac.settings.ui.SettingsNavigationRow
import com.thomaskioko.tvmaniac.settings.ui.SettingsSectionLabel
import com.thomaskioko.tvmaniac.settings.ui.loggedInState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun SettingsRootContent(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        state.rootGroups.forEach { group ->
            item { SettingsSectionLabel(text = group.label) }

            item {
                SettingsGroup {
                    group.items.forEachIndexed { index, categoryItem ->
                        SettingsNavigationRow(
                            modifier = Modifier.testTag(rootRowTestTag(categoryItem.page)),
                            icon = rootRowIcon(categoryItem.page),
                            title = categoryItem.title,
                            description = categoryItem.summary,
                            onClick = { onAction(OpenSettingsPage(categoryItem.page)) },
                        )
                        if (index != group.items.lastIndex) {
                            SettingsGroupDivider()
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

private fun rootRowTestTag(page: SettingsPage): String = when (page) {
    SettingsPage.APPEARANCE -> SettingsTestTags.GENERAL_APPEARANCE_ROW_TEST_TAG
    SettingsPage.LAYOUT -> SettingsTestTags.GENERAL_LAYOUT_ROW_TEST_TAG
    SettingsPage.BEHAVIOR -> SettingsTestTags.GENERAL_BEHAVIOR_ROW_TEST_TAG
    SettingsPage.NOTIFICATIONS -> SettingsTestTags.GENERAL_NOTIFICATIONS_ROW_TEST_TAG
    SettingsPage.PRIVACY -> SettingsTestTags.GENERAL_PRIVACY_ROW_TEST_TAG
    SettingsPage.INFO -> SettingsTestTags.ABOUT_INFO_ROW_TEST_TAG
    SettingsPage.LICENSES -> SettingsTestTags.ABOUT_LICENSES_ROW_TEST_TAG
    SettingsPage.ACCOUNT -> SettingsTestTags.ACCOUNT_TRAKT_ROW_TEST_TAG
    SettingsPage.ROOT -> ""
}

private fun rootRowIcon(page: SettingsPage): ImageVector = when (page) {
    SettingsPage.APPEARANCE -> Icons.Filled.Palette
    SettingsPage.LAYOUT -> Icons.Filled.Dashboard
    SettingsPage.BEHAVIOR -> Icons.Filled.Tune
    SettingsPage.NOTIFICATIONS -> Icons.Filled.Notifications
    SettingsPage.PRIVACY -> Icons.Filled.Security
    SettingsPage.INFO -> Icons.Filled.Info
    SettingsPage.LICENSES -> Icons.Filled.Description
    SettingsPage.ACCOUNT -> Icons.Filled.Person
    SettingsPage.ROOT -> Icons.Filled.Info
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun SettingsRootContentPreview() {
    SettingsRootContent(
        state = loggedInState,
        onAction = {},
    )
}
