package com.thomaskioko.tvmaniac.debug.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.components.ThemePreviews
import com.thomaskioko.tvmaniac.compose.components.TvManiacTopBar
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.debug.presenter.BackClicked
import com.thomaskioko.tvmaniac.debug.presenter.DebugPresenter
import com.thomaskioko.tvmaniac.i18n.MR.strings.cd_back
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_coming_soon
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_menu_title
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_section_background_tasks
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_debug_section_notifications
import com.thomaskioko.tvmaniac.i18n.resolve

@Composable
public fun DebugMenuScreen(
    presenter: DebugPresenter,
    modifier: Modifier = Modifier,
) {
    DebugMenuScreen(
        onBackClicked = { presenter.dispatch(BackClicked) },
        modifier = modifier,
    )
}

@Composable
internal fun DebugMenuScreen(
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

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
        modifier = modifier,
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
                    title = label_debug_section_notifications.resolve(context),
                    subtitle = label_debug_coming_soon.resolve(context),
                    onClick = { },
                )
            }

            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }

            item {
                DebugClickableItem(
                    icon = Icons.Filled.Sync,
                    title = label_debug_section_background_tasks.resolve(context),
                    subtitle = label_debug_coming_soon.resolve(context),
                    onClick = { },
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
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
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@ThemePreviews
@Composable
private fun DebugMenuScreenPreview() {
    TvManiacTheme {
        Surface {
            DebugMenuScreen(
                onBackClicked = {},
            )
        }
    }
}
