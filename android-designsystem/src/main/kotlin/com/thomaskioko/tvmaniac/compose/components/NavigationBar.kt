package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_discover
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_library
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_search
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_settings
import com.thomaskioko.tvmaniac.i18n.resolve

@Composable
public fun TvManiacNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        contentColor = NavigationDefaultColors.navigationContentColor(),
        tonalElevation = 8.dp,
        content = content,
    )
}

@Composable
public fun RowScope.TvManiacBottomNavigationItem(
    imageVector: ImageVector,
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    NavigationBarItem(
        modifier = modifier,
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = title,
            )
        },
        label = { Text(title) },
        selected = selected,
        alwaysShowLabel = true,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = NavigationDefaultColors.navigationSelectedItemColor(),
            unselectedIconColor = NavigationDefaultColors.navigationContentColor(),
            selectedTextColor = NavigationDefaultColors.navigationSelectedItemColor(),
            unselectedTextColor = NavigationDefaultColors.navigationContentColor(),
            indicatorColor = Color.Transparent,
        ),
        onClick = onClick,
    )
}

internal object NavigationDefaultColors {
    @Composable
    internal fun navigationContentColor(): Color = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    internal fun navigationSelectedItemColor(): Color = MaterialTheme.colorScheme.secondary
}

@ThemePreviews
@Composable
private fun TvManiacTvManiacNavigationBarPreviewPreview() {
    TvManiacTheme {
        Surface {
            TvManiacNavigationBar {
                TvManiacBottomNavigationItem(
                    imageVector = Icons.Outlined.Movie,
                    title = menu_item_discover.resolve(LocalContext.current),
                    selected = true,
                    onClick = { },
                )

                TvManiacBottomNavigationItem(
                    imageVector = Icons.Outlined.Search,
                    title = menu_item_search.resolve(LocalContext.current),
                    selected = false,
                    onClick = { },
                )

                TvManiacBottomNavigationItem(
                    imageVector = Icons.Outlined.VideoLibrary,
                    title = menu_item_library.resolve(LocalContext.current),
                    selected = false,
                    onClick = { },
                )

                TvManiacBottomNavigationItem(
                    imageVector = Icons.Outlined.Settings,
                    title = menu_item_settings.resolve(LocalContext.current),
                    selected = false,
                    onClick = { },
                )
            }
        }
    }
}
