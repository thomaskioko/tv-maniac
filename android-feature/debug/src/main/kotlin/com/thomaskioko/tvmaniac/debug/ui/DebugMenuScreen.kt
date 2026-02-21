package com.thomaskioko.tvmaniac.debug.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.debug.presenter.BackClicked
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.debug.presenter.DebugState
import com.thomaskioko.tvmaniac.debug.presenter.DismissSnackbar
import com.thomaskioko.tvmaniac.debug.presenter.TriggerDebugNotification
import com.thomaskioko.tvmaniac.debug.presenter.TriggerDelayedDebugNotification
import com.thomaskioko.tvmaniac.debug.presenter.TriggerLibrarySync
import com.thomaskioko.tvmaniac.debug.presenter.TriggerUpNextSync
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_library_sync_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_menu_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_never_synced
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_sync_login_required
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_upnext_sync_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_debug_notification_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_debug_notification_scheduled
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_delayed_debug_notification_description
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_delayed_debug_notification_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_episode_notifications
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_settings_last_sync_date
import com.thomaskioko.tvmaniac.i18n.resolve

@Composable
public fun DebugMenuScreen(
    presenter: DebugPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    DebugMenuScreen(
        state = state,
        onBackClicked = { presenter.dispatch(BackClicked) },
        onTriggerDebugNotification = { presenter.dispatch(TriggerDebugNotification) },
        onTriggerDelayedDebugNotification = { presenter.dispatch(TriggerDelayedDebugNotification) },
        onTriggerLibrarySync = { presenter.dispatch(TriggerLibrarySync) },
        onTriggerUpNextSync = { presenter.dispatch(TriggerUpNextSync) },
        onDismissSnackbar = { presenter.dispatch(DismissSnackbar(it)) },
        modifier = modifier,
    )
}

@Composable
internal fun DebugMenuScreen(
    state: DebugState,
    onBackClicked: () -> Unit,
    onTriggerDebugNotification: () -> Unit,
    onTriggerDelayedDebugNotification: () -> Unit,
    onTriggerLibrarySync: () -> Unit,
    onTriggerUpNextSync: () -> Unit,
    onDismissSnackbar: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var infoMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = modifier) {
        Scaffold(
            topBar = {
                TvManiacTopBar(
                    navigationIcon = {
                        Icon(
                            modifier = Modifier
                                .clickable(onClick = onBackClicked)
                                .padding(16.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = cd_back.resolve(context),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                    title = {
                        Text(
                            text = label_debug_menu_title.resolve(context),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp),
                        )
                    },
                    modifier = Modifier,
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.background,
                    ),
                )
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }

                item {
                    DebugClickableItem(
                        icon = Icons.Filled.Notifications,
                        title = label_settings_episode_notifications.resolve(context),
                        subtitle = label_settings_debug_notification_description.resolve(context),
                        isLoading = state.isSchedulingDebugNotification,
                        onClick = onTriggerDebugNotification,
                    )
                }

                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                }

                item {
                    DebugClickableItem(
                        icon = Icons.Filled.Schedule,
                        title = label_settings_delayed_debug_notification_title.resolve(context),
                        subtitle = label_settings_delayed_debug_notification_description.resolve(context),
                        isLoading = state.isSchedulingDebugNotification,
                        onClick = {
                            onTriggerDelayedDebugNotification()
                            infoMessage = label_settings_debug_notification_scheduled.resolve(context)
                        },
                    )
                }

                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                }

                item {
                    DebugClickableItem(
                        icon = Icons.Filled.Sync,
                        title = label_debug_library_sync_title.resolve(context),
                        subtitle = state.lastLibrarySyncDate
                            ?.let { stringResource(label_settings_last_sync_date.resourceId, it) }
                            ?: label_debug_never_synced.resolve(context),
                        isLoading = state.isSyncingLibrary,
                        onClick = {
                            if (state.isLoggedIn) {
                                onTriggerLibrarySync()
                            } else {
                                infoMessage = label_debug_sync_login_required.resolve(context)
                            }
                        },
                    )
                }

                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                }

                item {
                    DebugClickableItem(
                        icon = Icons.Filled.Refresh,
                        title = label_debug_upnext_sync_title.resolve(context),
                        subtitle = state.lastUpNextSyncDate
                            ?.let { stringResource(label_settings_last_sync_date.resourceId, it) }
                            ?: label_debug_never_synced.resolve(context),
                        isLoading = state.isSyncingUpNext,
                        onClick = {
                            if (state.isLoggedIn) {
                                onTriggerUpNextSync()
                            } else {
                                infoMessage = label_debug_sync_login_required.resolve(context)
                            }
                        },
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }

        TvManiacSnackBarHost(
            message = state.message?.message,
            style = SnackBarStyle.Error,
            onDismiss = { state.message?.let { onDismissSnackbar(it.id) } },
        )

        if (infoMessage != null) {
            TvManiacSnackBarHost(
                message = infoMessage,
                style = SnackBarStyle.Info,
                onDismiss = { infoMessage = null },
            )
        }
    }
}

@Composable
private fun DebugClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun DebugMenuScreenPreview() {
    TvManiacTheme {
        Surface {
            DebugMenuScreen(
                state = DebugState.DEFAULT_STATE,
                onBackClicked = {},
                onTriggerDebugNotification = {},
                onTriggerDelayedDebugNotification = {},
                onTriggerLibrarySync = {},
                onTriggerUpNextSync = {},
                onDismissSnackbar = {},
            )
        }
    }
}
