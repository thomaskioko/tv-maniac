package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import com.thomaskioko.tvmaniac.compose.components.SwitchRow
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.theme.TvManiacSpacing
import com.thomaskioko.tvmaniac.settings.presenter.BackgroundSyncToggled
import com.thomaskioko.tvmaniac.settings.presenter.CustomWatchDateToggled
import com.thomaskioko.tvmaniac.settings.presenter.IncludeSpecialsToggled
import com.thomaskioko.tvmaniac.settings.presenter.MultiplePlaysToggled
import com.thomaskioko.tvmaniac.settings.presenter.QuickRateToggled
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.presenter.YoutubeToggled
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroupDivider
import com.thomaskioko.tvmaniac.settings.ui.behaviorLockedState
import com.thomaskioko.tvmaniac.settings.ui.behaviorSimklFreeTierState
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

    val multiplePlaysDescription = buildString {
        append(state.labels.multiplePlaysDescription)
        state.multiplePlaysSyncNotice?.let {
            append("\n")
            append(it)
        }
    }

    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(TvManiacSpacing.medium)) }

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
                    modifier = Modifier.testTag(SettingsTestTags.CUSTOM_WATCH_DATE_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.EditCalendar,
                    title = state.labels.customWatchDateTitle,
                    description = state.labels.customWatchDateDescription,
                    checked = state.customWatchDateEnabled,
                    onCheckedChange = { onAction(CustomWatchDateToggled(it)) },
                )
                SettingsGroupDivider()
                SwitchRow(
                    modifier = Modifier.testTag(SettingsTestTags.QUICK_RATE_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.StarRate,
                    title = state.labels.quickRateTitle,
                    description = state.labels.quickRateDescription,
                    checked = state.quickRateEnabled,
                    onCheckedChange = { onAction(QuickRateToggled(it)) },
                    locked = state.premium.quickRateLocked,
                    lockedBadgeText = state.premium.badgeText,
                )
                SettingsGroupDivider()
                SwitchRow(
                    modifier = Modifier.testTag(SettingsTestTags.MULTIPLE_PLAYS_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.Replay,
                    title = state.labels.multiplePlaysTitle,
                    description = multiplePlaysDescription,
                    checked = state.multiplePlaysEnabled,
                    onCheckedChange = { onAction(MultiplePlaysToggled(it)) },
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

        item { Spacer(modifier = Modifier.height(TvManiacSpacing.large)) }
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

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun BehaviorPageSimklFreeTierPreview() {
    BehaviorPage(
        state = behaviorSimklFreeTierState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun BehaviorPageLockedPreview() {
    BehaviorPage(
        state = behaviorLockedState,
        onAction = {},
    )
}
