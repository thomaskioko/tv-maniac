package com.thomaskioko.tvmaniac.settings.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.settings.presenter.EpisodeNotificationsToggled
import com.thomaskioko.tvmaniac.settings.presenter.SettingsActions
import com.thomaskioko.tvmaniac.settings.presenter.SettingsState
import com.thomaskioko.tvmaniac.settings.ui.SettingsGroup
import com.thomaskioko.tvmaniac.settings.ui.SettingsSwitchRow
import com.thomaskioko.tvmaniac.settings.ui.notificationsLockedState
import com.thomaskioko.tvmaniac.settings.ui.notificationsState
import com.thomaskioko.tvmaniac.testtags.settings.SettingsTestTags

@Composable
internal fun NotificationsPage(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.testTag(SettingsTestTags.LIST_TEST_TAG)) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            SettingsGroup {
                SettingsSwitchRow(
                    modifier = Modifier.testTag(SettingsTestTags.EPISODE_NOTIFICATIONS_TOGGLE_TEST_TAG),
                    icon = Icons.Filled.Notifications,
                    title = state.labels.episodeNotificationsTitle,
                    description = state.labels.episodeNotificationsDescription,
                    checked = state.episodeNotificationsEnabled,
                    onCheckedChange = { onAction(EpisodeNotificationsToggled(it)) },
                    locked = state.locks.episodeNotificationsLocked,
                    lockedBadgeText = state.locks.badgeText,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun NotificationsPagePreview() {
    NotificationsPage(
        state = notificationsState,
        onAction = {},
    )
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun NotificationsPageLockedPreview() {
    NotificationsPage(
        state = notificationsLockedState,
        onAction = {},
    )
}
