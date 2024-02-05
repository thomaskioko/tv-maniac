package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun TvManiacNavigationBar(
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit,
) {
  NavigationBar(
    modifier = modifier,
    contentColor = NavigationDefaultColors.navigationContentColor(),
    tonalElevation = 8.dp,
    content = content,
  )
}

@Composable
fun RowScope.TvManiacBottomNavigationItem(
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
    colors =
      NavigationBarItemDefaults.colors(
        selectedIconColor = NavigationDefaultColors.navigationSelectedItemColor(),
        unselectedIconColor = NavigationDefaultColors.navigationContentColor(),
        selectedTextColor = NavigationDefaultColors.navigationSelectedItemColor(),
        unselectedTextColor = NavigationDefaultColors.navigationContentColor(),
        indicatorColor = NavigationDefaultColors.navigationIndicatorColor(),
      ),
    onClick = onClick,
  )
}

object NavigationDefaultColors {
  @Composable fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

  @Composable fun navigationSelectedItemColor() = MaterialTheme.colorScheme.secondary

  @Composable fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}
