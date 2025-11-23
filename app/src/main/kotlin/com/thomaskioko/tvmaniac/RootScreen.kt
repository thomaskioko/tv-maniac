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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.thomaskioko.tvmaniac.home.ui.HomeScreen
import com.thomaskioko.tvmaniac.moreshows.ui.MoreShowsScreen
import com.thomaskioko.tvmaniac.navigation.RootPresenter
import com.thomaskioko.tvmaniac.profile.ui.ProfileScreen
import com.thomaskioko.tvmaniac.seasondetails.ui.SeasonDetailsScreen
import com.thomaskioko.tvmaniac.settings.ui.SettingsScreen
import com.thomaskioko.tvmaniac.showdetails.ui.ShowDetailsScreen
import com.thomaskioko.tvmaniac.trailers.ui.TrailersScreen

@Composable
fun RootScreen(rootPresenter: RootPresenter, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
        ) {
            ChildrenContent(rootPresenter = rootPresenter, modifier = Modifier.weight(1F))
        }
    }
}

@Composable
private fun ChildrenContent(rootPresenter: RootPresenter, modifier: Modifier = Modifier) {
    val childStack by rootPresenter.childStack.collectAsStateWithLifecycle()

    Children(
        modifier = modifier,
        stack = childStack,
    ) { child ->
        val fillMaxSizeModifier = Modifier.fillMaxSize()
        when (val screen = child.instance) {
            is RootPresenter.Child.Home ->
                HomeScreen(presenter = screen.presenter, modifier = fillMaxSizeModifier)
            is RootPresenter.Child.Profile ->
                ProfileScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            is RootPresenter.Child.Settings ->
                SettingsScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            is RootPresenter.Child.ShowDetails -> {
                ShowDetailsScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            }
            is RootPresenter.Child.SeasonDetails -> {
                SeasonDetailsScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            }
            is RootPresenter.Child.Trailers ->
                TrailersScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            is RootPresenter.Child.MoreShows ->
                MoreShowsScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            RootPresenter.Child.GenreShows -> {
                // TODO:: Genre Shows Screen
            }
        }
    }
}
