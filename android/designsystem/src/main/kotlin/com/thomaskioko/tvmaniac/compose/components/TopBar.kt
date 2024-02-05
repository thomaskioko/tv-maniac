package com.thomaskioko.tvmaniac.compose.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.thomaskioko.tvmaniac.compose.extensions.iconButtonBackgroundScrim
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.resources.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvManiacTopBar(
  modifier: Modifier = Modifier,
  elevation: Dp = 0.dp,
  colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsableAppBar(
  title: String?,
  showAppBarBackground: Boolean,
  onNavIconPressed: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val backgroundColor by
    animateColorAsState(
      targetValue =
        when {
          showAppBarBackground -> MaterialTheme.colorScheme.surface
          else -> Color.Transparent
        },
      animationSpec = spring(),
      label = "backgroundColorAnimation",
    )

  val elevation by
    animateDpAsState(
      targetValue =
        when {
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
            style =
              MaterialTheme.typography.titleMedium.copy(
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
          contentDescription = stringResource(R.string.cd_navigate_back),
          tint = MaterialTheme.colorScheme.onBackground,
        )
      }
    },
    colors =
      TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = backgroundColor,
      ),
    modifier = modifier.shadow(elevation = elevation),
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun TopBarPreview() {
  TvManiacTheme {
    TvManiacTopBar(
      title = {
        Text(
          text = "Tv Maniac",
          style =
            MaterialTheme.typography.titleSmall.copy(
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

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun TopBarActionPreview() {
  TvManiacTheme {
    TvManiacTopBar(
      title = {
        Text(
          text = "Tv Maniac",
          style =
            MaterialTheme.typography.titleSmall.copy(
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

@OptIn(ExperimentalMaterial3Api::class)
@ThemePreviews
@Composable
private fun TopBarScrimPreview() {
  TvManiacTheme {
    TvManiacTopBar(
      title = {
        Text(
          text = "Tv Maniac",
          style =
            MaterialTheme.typography.titleSmall.copy(
              color = MaterialTheme.colorScheme.onSurface,
            ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier.fillMaxWidth(),
        )
      },
      navigationIcon = {
        Image(
          imageVector = Icons.Filled.ArrowBack,
          contentDescription = null,
          colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurface),
          modifier = Modifier.clickable(onClick = {}).padding(16.dp),
        )
      },
      modifier = Modifier.iconButtonBackgroundScrim(),
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
      onNavIconPressed = {},
    )
  }
}
