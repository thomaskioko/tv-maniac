package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.SwitchRow
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.settings.presenter.CrashReportingToggled
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroupDivider
import com.thomaskioko.tvmaniac.settings.ui.SettingsNavigationRow
import com.thomaskioko.tvmaniac.settings.ui.openInCustomTab
import com.thomaskioko.tvmaniac.settings.ui.privacyState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun PrivacyPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(TvManiacSpacing.medium)) }

        item {
            SettingsGroup {
                SwitchRow(
                    icon = Icons.Filled.BugReport,
                    title = state.labels.crashReportingTitle,
                    description = state.labels.crashReportingDescription,
                    checked = state.crashReportingEnabled,
                    onCheckedChange = { onAction(CrashReportingToggled(it)) },
                )
                SettingsGroupDivider()
                SettingsNavigationRow(
                    icon = Icons.Filled.Security,
                    title = state.labels.privacyPolicy,
                    onClick = { openInCustomTab(context, state.privacyPolicyUrl) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(TvManiacSpacing.large)) }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun PrivacyPagePreview() {
    PrivacyPage(
        state = privacyState,
        onAction = {},
    )
}
