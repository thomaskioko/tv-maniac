package com.thomaskioko.tvmaniac.compose.components

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.thomaskioko.tvmaniac.compose.extensions.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import kotlin.math.roundToInt

@Composable
public fun TvManiacTopBar(
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier.shadow(elevation = elevation),
        scrollBehavior = scrollBehavior,
        title = title,
        navigationIcon = navigationIcon,
        colors = colors,
        actions = actions,
    )
}

@Composable
public fun RefreshCollapsableTopAppBar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    isRefreshing: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    actionIcon: @Composable (() -> Unit)? = null,
    onNavIconClicked: () -> Unit = {},
    onActionIconClicked: () -> Unit = {},
) {
    var appBarHeight by remember { mutableIntStateOf(0) }
    val showAppBarBackground by remember {
        derivedStateOf {
            val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
            when {
                visibleItemsInfo.isEmpty() -> false
                appBarHeight <= 0 -> false
                else -> {
                    val firstVisibleItem = visibleItemsInfo[0]
                    when {
                        firstVisibleItem.index > 0 -> true
                        else -> firstVisibleItem.size + firstVisibleItem.offset - 5 <= appBarHeight
                    }
                }
            }
        }
    }

    RefreshCollapsableTopAppBar(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { appBarHeight = it.height },
        title = title,
        navigationIcon = navigationIcon,
        actionIcon = actionIcon,
        showAppBarBackground = showAppBarBackground,
        scrollBehavior = scrollBehavior,
        onActionClicked = onActionIconClicked,
        onNavIconPressed = onNavIconClicked,
        isRefreshing = isRefreshing,
    )
}

@Composable
public fun RefreshCollapsableTopAppBar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.(Boolean) -> Unit = {},
) {
    var appBarHeight by remember { mutableIntStateOf(0) }
    val showAppBarBackground by remember {
        derivedStateOf {
            val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
            when {
                visibleItemsInfo.isEmpty() -> false
                appBarHeight <= 0 -> false
                else -> {
                    val firstVisibleItem = visibleItemsInfo[0]
                    when {
                        firstVisibleItem.index > 0 -> true
                        else -> firstVisibleItem.size + firstVisibleItem.offset - 5 <= appBarHeight
                    }
                }
            }
        }
    }

    RefreshCollapsableTopAppBar(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { appBarHeight = it.height },
        scrollBehavior = scrollBehavior,
        title = title,
        navigationIcon = navigationIcon,
        showAppBarBackground = showAppBarBackground,
        actions = { actions(showAppBarBackground) },
    )
}

@Composable
internal fun RefreshCollapsableTopAppBar(
    showAppBarBackground: Boolean,
    scrollBehavior: TopAppBarScrollBehavior?,
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)?,
    actions: @Composable RowScope.(Boolean) -> Unit,
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
                targetState = showAppBarBackground,
                label = "titleAnimation",
            ) { show ->
                if (show) title()
            }
        },
        navigationIcon = navigationIcon ?: {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
        ),
        actions = { actions(showAppBarBackground) },
        modifier = modifier.shadow(elevation = elevation),
        scrollBehavior = scrollBehavior,
    )
}

@Composable
internal fun RefreshCollapsableTopAppBar(
    showAppBarBackground: Boolean,
    isRefreshing: Boolean,
    scrollBehavior: TopAppBarScrollBehavior?,
    onActionClicked: () -> Unit,
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)?,
    actionIcon: @Composable (() -> Unit)?,
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
        modifier = modifier.shadow(elevation = elevation),
        title = {
            Crossfade(
                targetState = showAppBarBackground,
                label = "titleAnimation",
            ) { show ->
                if (show) title()
            }
        },
        navigationIcon = {
            if (navigationIcon != null) {
                ScrimButton(
                    show = showAppBarBackground,
                    onClick = onNavIconPressed,
                ) {
                    navigationIcon()
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
        ),
        actions = {
            if (isRefreshing || actionIcon != null) {
                ScrimButton(
                    show = showAppBarBackground,
                    onClick = onActionClicked,
                ) {
                    RefreshButton(
                        modifier = Modifier
                            .size(20.dp)
                            .padding(2.dp),
                        isRefreshing = isRefreshing,
                        content = actionIcon ?: {},
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun AutoSizedCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    BoxWithConstraints(modifier) {
        val diameter = with(LocalDensity.current) {
            // We need to minus the padding added within CircularProgressIndicator
            min(constraints.maxWidth.toDp(), constraints.maxHeight.toDp()) - 4.dp
        }

        CircularProgressIndicator(
            strokeWidth = (diameter.value * (4.dp / 40.dp)).roundToInt().dp.coerceAtLeast(2.dp),
            color = color,
        )
    }
}

@ThemePreviews
@Composable
private fun TopBarPreview() {
    TvManiacTheme {
        TvManiacTopBar(
            title = {
                Text(
                    text = "Tv Maniac",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
        )
    }
}

public inline fun actionIconWhen(
    visible: Boolean,
    crossinline content: @Composable () -> Unit,
): (@Composable () -> Unit)? = if (visible) {
    { content() }
} else {
    null
}

@ThemePreviews
@Composable
private fun TopBarActionPreview() {
    TvManiacTheme {
        TvManiacTopBar(
            title = {
                Text(
                    text = "Tv Maniac",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            actions = {
                IconButton(
                    onClick = {},
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            },
        )
    }
}

@ThemePreviews
@Composable
private fun TopBarScrimPreview() {
    TvManiacTheme {
        TvManiacTopBar(
            title = {
                Text(
                    text = "Tv Maniac",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            navigationIcon = {
                Image(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface),
                    modifier = Modifier
                        .clickable(onClick = {})
                        .padding(16.dp),
                )
            },
            modifier = Modifier.iconButtonBackgroundScrim(enabled = true, alpha = 0.4f),
        )
    }
}
