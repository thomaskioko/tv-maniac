package com.thomaskioko.tvmaniac

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomNavigationItem
import com.thomaskioko.tvmaniac.compose.components.TvManiacNavigationBar
import com.thomaskioko.tvmaniac.navigation.RootNavigationPresenter
import com.thomaskioko.tvmaniac.navigation.RootNavigationPresenter.Config
import com.thomaskioko.tvmaniac.navigation.Screen
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.search.ui.SearchScreen
import com.thomaskioko.tvmaniac.seasondetails.ui.SeasonDetailsScreen
import com.thomaskioko.tvmaniac.ui.discover.DiscoverScreen
import com.thomaskioko.tvmaniac.ui.library.LibraryScreen
import com.thomaskioko.tvmaniac.ui.moreshows.MoreShowsScreen
import com.thomaskioko.tvmaniac.ui.settings.SettingsScreen
import com.thomaskioko.tvmaniac.ui.showdetails.ShowDetailsScreen
import com.thomaskioko.tvmaniac.ui.trailers.videoplayer.TrailersScreen

@Composable
fun RootScreen(presenter: RootNavigationPresenter, modifier: Modifier = Modifier) {
  Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
    Column(
      modifier =
        Modifier.fillMaxSize()
          .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
    ) {
      ChildrenContent(presenter = presenter, modifier = Modifier.weight(1F))
      BottomNavigationContent(presenter = presenter, modifier = Modifier.fillMaxWidth())
    }
  }
}

@Composable
private fun ChildrenContent(presenter: RootNavigationPresenter, modifier: Modifier = Modifier) {
  val childStack by presenter.screenStackFlow.collectAsState()

  Children(
    modifier = modifier,
    stack = childStack,
  ) { child ->
    val fillMaxSizeModifier = Modifier.fillMaxSize()
    when (val screen = child.instance) {
      is Screen.Discover ->
        DiscoverScreen(
          discoverShowsPresenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      is Screen.Library ->
        LibraryScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      is Screen.Search ->
        SearchScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      is Screen.Settings ->
        SettingsScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      is Screen.ShowDetails ->
        ShowDetailsScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      is Screen.SeasonDetails ->
        SeasonDetailsScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      is Screen.Trailers ->
        TrailersScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
      is Screen.MoreShows ->
        MoreShowsScreen(
          presenter = screen.presenter,
          modifier = fillMaxSizeModifier,
        )
    }
  }
}

@Composable
internal fun BottomNavigationContent(
  presenter: RootNavigationPresenter,
  modifier: Modifier = Modifier,
) {
  val childStack by presenter.screenStackFlow.collectAsState()
  val activeComponent = childStack.active.instance

  val showBottomBar = presenter.shouldShowBottomNav(activeComponent)

  AnimatedVisibility(
    visible = showBottomBar,
    enter = fadeIn(),
    exit = slideOutVertically() + shrinkVertically() + fadeOut(),
  ) {
    TvManiacNavigationBar(
      modifier = modifier,
    ) {
      TvManiacBottomNavigationItem(
        imageVector = Icons.Outlined.Movie,
        title = stringResource(id = R.string.menu_item_discover),
        selected = activeComponent is Screen.Discover,
        onClick = { presenter.bringToFront(Config.Discover) },
      )

      TvManiacBottomNavigationItem(
        imageVector = Icons.Outlined.Search,
        title = stringResource(id = R.string.menu_item_search),
        selected = activeComponent is Screen.Search,
        onClick = { presenter.bringToFront(Config.Search) },
      )

      TvManiacBottomNavigationItem(
        imageVector = Icons.Outlined.VideoLibrary,
        title = stringResource(id = R.string.menu_item_library),
        selected = activeComponent is Screen.Library,
        onClick = { presenter.bringToFront(Config.Library) },
      )

      TvManiacBottomNavigationItem(
        imageVector = Icons.Outlined.Settings,
        title = stringResource(id = R.string.menu_item_settings),
        selected = activeComponent is Screen.Settings,
        onClick = { presenter.bringToFront(Config.Settings) },
      )
    }
  }
}
