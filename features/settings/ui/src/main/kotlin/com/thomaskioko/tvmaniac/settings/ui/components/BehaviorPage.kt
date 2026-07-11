package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.SwitchRow
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.settings.presenter.BackgroundSyncToggled
import com.thomaskioko.tvmaniac.settings.presenter.IncludeSpecialsToggled
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.YoutubeToggled
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroupDivider
import com.thomaskioko.tvmaniac.settings.ui.behaviorState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun BehaviorPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val syncDescription = buildString {
        append(state.labels.syncDescription)
        state.labels.lastSync?.let {
            append("\n")
            append(it)
        }
    }

    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsGroup {
                SwitchRow(
                    icon = Icons.Filled.Sync,
                    title = state.labels.syncTitle,
                    description = syncDescription,
                    checked = state.backgroundSyncEnabled,
                    onCheckedChange = { onAction(BackgroundSyncToggled(it)) },
                )
                SettingsGroupDivider()
                SwitchRow(
                    icon = Icons.Filled.VideoLibrary,
                    title = state.labels.includeSpecialsTitle,
                    description = state.labels.includeSpecialsDescription,
                    checked = state.includeSpecials,
                    onCheckedChange = { onAction(IncludeSpecialsToggled(it)) },
                )
                SettingsGroupDivider()
                SwitchRow(
                    icon = Icons.Filled.Tv,
                    title = state.labels.youtubeTitle,
                    description = state.labels.youtubeDescription,
                    checked = state.openTrailersInYoutube,
                    onCheckedChange = { onAction(YoutubeToggled(it)) },
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun BehaviorPagePreview() {
    BehaviorPage(
        state = behaviorState,
        onAction = {},
    )
}
