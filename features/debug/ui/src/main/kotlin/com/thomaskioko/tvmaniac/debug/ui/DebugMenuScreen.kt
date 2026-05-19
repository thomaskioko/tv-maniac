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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewWrapper
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.SnackBarStyle
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacPreviewWrapperProvider
import com.thomaskioko.tvmaniac.compose.components.TvManiacSnackBarHost
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.debug.presenter.BackClicked
import com.thomaskioko.tvmaniac.debug.presenter.DebugActions
import com.thomaskioko.tvmaniac.debug.presenter.DebugItem
import com.thomaskioko.tvmaniac.debug.presenter.DebugItemIcon
import com.thomaskioko.tvmaniac.debug.presenter.DebugItemRole
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.debug.presenter.DebugState
import com.thomaskioko.tvmaniac.debug.presenter.DismissSnackbar
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.resolve
import io.github.thomaskioko.codegen.annotations.ScreenUi

@ScreenUi(presenter = DebugPresenter::class, parentScope = ActivityScope::class)
@Composable
public fun DebugMenuScreen(
    presenter: DebugPresenter,
    modifier: Modifier = Modifier,
) {
    val state by presenter.state.collectAsState()

    DebugMenuScreen(
        state = state,
        onAction = { presenter.dispatch(it) },
        modifier = modifier,
    )
}

@Composable
internal fun DebugMenuScreen(
    state: DebugState,
    onAction: (DebugActions) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Box(modifier = modifier) {
        Scaffold(
            topBar = {
                TvManiacTopBar(
                    navigationIcon = {
                        Icon(
                            modifier = Modifier
                                .clickable(onClick = { onAction(BackClicked) })
                                .padding(16.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = cd_back.resolve(context),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    },
                    title = {
                        Text(
                            text = state.title,
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
                    colors = TopAppBarDefaults.topAppBarColors(
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

                itemsIndexed(
                    items = state.items,
                    key = { _, it -> it.id },
                ) { index, item ->
                    DebugMenuItem(
                        item = item,
                        onClick = { item.action?.let(onAction) },
                    )
                    if (index < state.items.lastIndex) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            modifier = Modifier.padding(horizontal = 24.dp),
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }

        TvManiacSnackBarHost(
            message = state.message?.message,
            style = SnackBarStyle.Error,
            onDismiss = { state.message?.let { onAction(DismissSnackbar(it.id)) } },
        )
    }
}

@Composable
private fun DebugMenuItem(
    item: DebugItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconTint = when (item.role) {
        DebugItemRole.Accent -> MaterialTheme.colorScheme.secondary
        DebugItemRole.Destructive -> MaterialTheme.colorScheme.error
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = item.action != null && !item.isLoading, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = item.icon.toImageVector(),
            tint = iconTint,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = item.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        when {
            item.isLoading -> CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.secondary,
            )
            item.action != null -> Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun DebugItemIcon.toImageVector(): ImageVector = when (this) {
    DebugItemIcon.Notifications -> Icons.Filled.Notifications
    DebugItemIcon.Schedule -> Icons.Filled.Schedule
    DebugItemIcon.LibrarySync -> Icons.Filled.Sync
    DebugItemIcon.UpNextSync -> Icons.Filled.Refresh
    DebugItemIcon.FeatureFlags -> Icons.Filled.Flag
    DebugItemIcon.Key -> Icons.Filled.VpnKey
    DebugItemIcon.Warning -> Icons.Filled.Warning
}

@ThemePreviews
@PreviewWrapper(TvManiacPreviewWrapperProvider::class)
@Composable
private fun DebugMenuScreenPreview() {
    DebugMenuScreen(
        state = DebugState.DEFAULT_STATE,
        onAction = {},
    )
}
