package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.common.localization.MR
import com.thomaskioko.tvmaniac.compose.extensions.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvManiacTopBar(
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    title: String? = null,
    showNavigationIcon: Boolean = false,
    actionImageVector: ImageVector? = null,
    onActionClicked: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    TopAppBar(
        modifier = modifier
            .shadow(elevation = elevation),
        title = {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        navigationIcon = {
            if (showNavigationIcon) {
                Image(
                    painter = painterResource(R.drawable.ic_baseline_arrow_back_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .clickable(onClick = onBackClick)
                        .padding(16.dp),
                )
            }
        },
        colors = colors,
        actions = {
            if (actionImageVector != null) {
                IconButton(
                    onClick = onActionClicked,
                ) {
                    Icon(
                        imageVector = actionImageVector,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsableAppBar(
    title: String?,
    showAppBarBackground: Boolean,
    onNavIconPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            showAppBarBackground -> MaterialTheme.colorScheme.surface
            else -> Color.Transparent
        },
        animationSpec = spring(),
        label = "backgroundColorAnimation",
    )

    val elevation by animateDpAsState(
        targetValue = when {
            showAppBarBackground -> 4.dp
            else -> 0.dp
        },
        animationSpec = spring(),
        label = "elevationAnimation",
    )

    TopAppBar(
        title = {
            Crossfade(
                targetState = showAppBarBackground && title != null,
                label = "titleAnimation",
            ) { show ->
                if (show) {
                    Text(
                        text = title ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavIconPressed,
                modifier = Modifier.iconButtonBackgroundScrim(enabled = !showAppBarBackground),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(MR.strings.cd_navigate_back.resourceId),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = backgroundColor,
        ),
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.systemBars
                    .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
            )
            .shadow(elevation = elevation),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun TopBarPreview() {
    TvManiacTheme {
        TvManiacTopBar(
            title = "Tv Maniac",
            showNavigationIcon = true,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun TopBarActionPreview() {
    TvManiacTheme {
        TvManiacTopBar(
            title = "Tv Maniac",
            showNavigationIcon = true,
            actionImageVector = Icons.Filled.Settings,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun TopBarScrimPreview() {
    TvManiacTheme {
        TvManiacTopBar(
            title = "Tv Maniac",
            showNavigationIcon = true,
            modifier = Modifier
                .iconButtonBackgroundScrim(),
        )
    }
}

@ThemePreviews
@Composable
private fun CollapsableAppBarPreview() {
    TvManiacTheme {
        CollapsableAppBar(
            title = "Star Wars",
            showAppBarBackground = true,
            onNavIconPressed = { },
        )
    }
}
