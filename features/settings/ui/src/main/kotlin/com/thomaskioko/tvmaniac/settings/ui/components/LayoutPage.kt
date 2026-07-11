package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.SwitchRow
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.settings.presenter.HapticFeedbackToggled
import com.thomaskioko.tvmaniac.settings.presenter.SeasonOrderToggled
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.layoutState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun LayoutPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsGroup {
                SwitchRow(
                    modifier = Modifier.testTag(SettingsTestTags.HAPTIC_FEEDBACK_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.Vibration,
                    title = state.labels.hapticFeedbackTitle,
                    description = state.labels.hapticFeedbackDescription,
                    checked = state.hapticFeedbackEnabled,
                    onCheckedChange = { onAction(HapticFeedbackToggled(it)) },
                )

                SwitchRow(
                    modifier = Modifier.testTag(SettingsTestTags.SEASON_ORDER_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.SwapVert,
                    title = state.labels.seasonOrderTitle,
                    description = state.labels.seasonOrderDescription,
                    checked = state.newestSeasonFirst,
                    onCheckedChange = { onAction(SeasonOrderToggled(it)) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun LayoutPagePreview() {
    LayoutPage(
        state = layoutState,
        onAction = {},
    )
}
