package com.thomaskioko.tvmaniac

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.thomaskioko.tvmaniac.home.HomeScreen
import com.thomaskioko.tvmaniac.navigation.RootComponent
import com.thomaskioko.tvmaniac.seasondetails.ui.SeasonDetailsScreen
import com.thomaskioko.tvmaniac.ui.moreshows.MoreShowsScreen
import com.thomaskioko.tvmaniac.ui.showdetails.ShowDetailsScreen
import com.thomaskioko.tvmaniac.ui.trailers.videoplayer.TrailersScreen

@Composable
fun RootScreen(rootComponent: RootComponent, modifier: Modifier = Modifier) {
  Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
    Column(
      modifier =
        Modifier.fillMaxSize()
          .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
    ) {
      ChildrenContent(rootComponent = rootComponent, modifier = Modifier.weight(1F))
    }
  }
}

@Composable
private fun ChildrenContent(rootComponent: RootComponent, modifier: Modifier = Modifier) {
  val childStack by rootComponent.stack.collectAsState()

  Children(
    modifier = modifier,
    stack = childStack,
  ) { child ->
    val fillMaxSizeModifier = Modifier.fillMaxSize()
    when (val screen = child.instance) {
      is RootComponent.Child.Home ->
        HomeScreen(presenter = screen.presenter, modifier = fillMaxSizeModifier)
      is RootComponent.Child.ShowDetails -> {
        ShowDetailsScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      }
      is RootComponent.Child.SeasonDetails -> {
        SeasonDetailsScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      }
      is RootComponent.Child.Trailers ->
        TrailersScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      is RootComponent.Child.MoreShows ->
        MoreShowsScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
    }
  }
}
