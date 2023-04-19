package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.ui.TopAppBar
import com.thomaskioko.tvmaniac.compose.extensions.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.resources.R

@Composable
fun TvManiacTopBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    showNavigationIcon: Boolean = false,
    actionImageVector: ImageVector? = null,
    onActionClicked: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    TopAppBar(
        title = {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
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
                    modifier = modifier
                        .clickable(onClick = onBackClick)
                        .padding(16.dp),
                )
            }
        },
        backgroundColor = MaterialTheme.colorScheme.background,
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
                showAppBarBackground && title != null,
                label = "titleAnimation",
            ) { show ->
                if (show) {
                    Text(
                        text = title!!,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                    )
                }
            }
        },
        contentPadding = WindowInsets.systemBars
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            .asPaddingValues(),
        navigationIcon = {
            IconButton(
                onClick = onNavIconPressed,
                modifier = Modifier.iconButtonBackgroundScrim(enabled = !showAppBarBackground),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.cd_navigate_back),
                )
            }
        },
        elevation = elevation,
        backgroundColor = backgroundColor,
        modifier = modifier,
    )
}

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
