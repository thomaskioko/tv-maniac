package com.thomaskioko.tvmaniac.compose.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.thomaskioko.tvmaniac.navigation.NavigationScreen

@Composable
fun RowScope.TvManiacBottomNavigationItem(
    screen: NavigationScreen,
    @DrawableRes icon: Int,
    title: String,
    selected: Boolean,
    onNavigationSelected: (NavigationScreen) -> Unit
) {
    BottomNavigationItem(
        icon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title
            )
        },
        label = { Text(title) },
        selected = selected,
        alwaysShowLabel = false,
        selectedContentColor = MaterialTheme.colors.secondary,
        unselectedContentColor = MaterialTheme.colors.onSurface,
        onClick = { onNavigationSelected(screen) }
    )
}