package com.thomaskioko.tvmaniac.home

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
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter.Child.Discover
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter.Child.Watchlist
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter.Child.Search
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter.Child.Settings
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.ui.search.SearchScreen
import com.thomaskioko.tvmaniac.ui.discover.DiscoverScreen
import com.thomaskioko.tvmaniac.ui.library.WatchlistScreen
import com.thomaskioko.tvmaniac.ui.settings.SettingsScreen

@Composable
fun HomeScreen(
  presenter: HomePresenter,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier) {
    ChildrenContent(homePresenter = presenter, modifier = Modifier.weight(1F))
    BottomNavigationContent(component = presenter, modifier = Modifier.fillMaxWidth())
  }
}

@Composable
private fun ChildrenContent(homePresenter: HomePresenter, modifier: Modifier = Modifier) {
  val childStack by homePresenter.homeChildStack.collectAsState()

  Children(
    modifier = modifier,
    stack = childStack,
  ) { child ->
    val fillMaxSizeModifier = Modifier.fillMaxSize()
    when (val screen = child.instance) {
      is Discover -> {
        DiscoverScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      }
      is Watchlist -> {
        WatchlistScreen(
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
  component: HomePresenter,
  modifier: Modifier = Modifier,
) {
  val childStack by component.homeChildStack.collectAsState()
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
      selected = activeComponent is Watchlist,
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
