package com.thomaskioko.tvmaniac.tabs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomNavigationItem
import com.thomaskioko.tvmaniac.compose.components.TvManiacNavigationBar
import com.thomaskioko.tvmaniac.presentation.home.HomeComponent
import com.thomaskioko.tvmaniac.presentation.home.HomeComponent.Child.Discover
import com.thomaskioko.tvmaniac.presentation.home.HomeComponent.Child.Library
import com.thomaskioko.tvmaniac.presentation.home.HomeComponent.Child.Search
import com.thomaskioko.tvmaniac.presentation.home.HomeComponent.Child.Settings
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.search.ui.SearchScreen
import com.thomaskioko.tvmaniac.ui.discover.DiscoverScreen
import com.thomaskioko.tvmaniac.ui.library.LibraryScreen
import com.thomaskioko.tvmaniac.ui.settings.SettingsScreen

@Composable
internal fun HomeContent(
  component: HomeComponent,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    ChildrenContent(homeComponent = component, modifier = Modifier.weight(1F))
    BottomNavigationContent(component = component, modifier = Modifier.fillMaxWidth())
  }
}

@Composable
private fun ChildrenContent(homeComponent: HomeComponent, modifier: Modifier = Modifier) {
  val childStack by homeComponent.stack.collectAsState()

  Children(
    modifier = modifier,
    stack = childStack,
  ) { child ->
    val fillMaxSizeModifier = Modifier.fillMaxSize()
    when (val screen = child.instance) {
      is Discover -> {
        DiscoverScreen(
          discoverShowsPresenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      }
      is Library -> {
        LibraryScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      }
      is Search -> {
        SearchScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      }
      is Settings -> {
        SettingsScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      }
    }
  }
}

@Composable
internal fun BottomNavigationContent(
  component: HomeComponent,
  modifier: Modifier = Modifier,
) {
  val childStack by component.stack.collectAsState()
  val activeComponent = childStack.active.instance

  TvManiacNavigationBar(
    modifier = modifier,
  ) {
    TvManiacBottomNavigationItem(
      imageVector = Icons.Outlined.Movie,
      title = stringResource(id = R.string.menu_item_discover),
      selected = activeComponent is Discover,
      onClick = { component.onDiscoverClicked() },
    )

    TvManiacBottomNavigationItem(
      imageVector = Icons.Outlined.Search,
      title = stringResource(id = R.string.menu_item_search),
      selected = activeComponent is Search,
      onClick = { component.onSearchClicked() },
    )

    TvManiacBottomNavigationItem(
      imageVector = Icons.Outlined.VideoLibrary,
      title = stringResource(id = R.string.menu_item_library),
      selected = activeComponent is Library,
      onClick = { component.onLibraryClicked() },
    )

    TvManiacBottomNavigationItem(
      imageVector = Icons.Outlined.Settings,
      title = stringResource(id = R.string.menu_item_settings),
      selected = activeComponent is Settings,
      onClick = { component.onSettingsClicked() },
    )
  }
}
