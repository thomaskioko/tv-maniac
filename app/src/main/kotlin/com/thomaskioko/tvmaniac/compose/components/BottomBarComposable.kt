package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.TabScreens

@Composable
fun RowScope.TvManiacBottomNavigationItem(
    screen: TabScreens,
    currentRoute: String,
    navController: NavHostController
) {
    BottomNavigationItem(
        icon = {
            Icon(
                painter = painterResource(id = screen.icon),
                contentDescription = null
            )
        },
        label = { Text(stringResource(id = screen.resourceId)) },
        selected = currentRoute == screen.route,
        alwaysShowLabel = false,
        selectedContentColor = MaterialTheme.colors.secondary,
        unselectedContentColor = MaterialTheme.colors.onSurface,
        onClick = {
            if (currentRoute != screen.route) {
                navController.navigate(screen.route)
            }
        }
    )
}