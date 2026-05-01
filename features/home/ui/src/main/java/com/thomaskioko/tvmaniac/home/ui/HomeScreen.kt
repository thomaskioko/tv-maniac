package com.thomaskioko.tvmaniac.home.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.thomaskioko.tvmaniac.compose.components.AvatarComponent
import com.thomaskioko.tvmaniac.compose.components.NavigationDefaultColors
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomNavigationItem
import com.thomaskioko.tvmaniac.compose.components.TvManiacNavigationBar
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.discover.ui.DiscoverScreen
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_discover
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_library
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_profile
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_progress
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presentation.library.LibraryPresenter
import com.thomaskioko.tvmaniac.presentation.progress.ProgressPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.profile.presenter.ProfilePresenter
import com.thomaskioko.tvmaniac.profile.ui.ProfileScreen
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import com.thomaskioko.tvmaniac.ui.library.LibraryScreen
import com.thomaskioko.tvmaniac.ui.progress.ProgressScreen
import io.github.thomaskioko.codegen.annotations.ScreenUi

@ScreenUi(presenter = HomePresenter::class, parentScope = ActivityScope::class)
@Composable
public fun HomeScreen(
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
        when (val presenter = child.instance.presenter) {
            is DiscoverShowsPresenter ->
                DiscoverScreen(presenter = presenter, modifier = fillMaxSizeModifier)
            is ProgressPresenter ->
                ProgressScreen(presenter = presenter, modifier = fillMaxSizeModifier)
            is LibraryPresenter ->
                LibraryScreen(presenter = presenter, modifier = fillMaxSizeModifier)
            is ProfilePresenter ->
                ProfileScreen(presenter = presenter, modifier = fillMaxSizeModifier)
        }
    }
}

@Composable
internal fun BottomNavigationContent(
    component: HomePresenter,
    modifier: Modifier = Modifier,
) {
    val childStack by component.homeChildStack.collectAsState()
    val activePresenter = childStack.active.instance.presenter
    val avatarUrl by component.profileAvatarUrl.collectAsState()
    val context = LocalContext.current

    TvManiacNavigationBar(
        modifier = modifier,
    ) {
        TvManiacBottomNavigationItem(
            modifier = Modifier.testTag(HomeTestTags.DISCOVER_TAB),
            imageVector = Icons.Outlined.Movie,
            title = menu_item_discover.resolve(context),
            selected = activePresenter is DiscoverShowsPresenter,
            onClick = { component.onDiscoverClicked() },
        )

        TvManiacBottomNavigationItem(
            modifier = Modifier.testTag(HomeTestTags.PROGRESS_TAB),
            imageVector = Icons.Outlined.PlayCircleOutline,
            title = menu_item_progress.resolve(context),
            selected = activePresenter is ProgressPresenter,
            onClick = { component.onProgressClicked() },
        )

        TvManiacBottomNavigationItem(
            modifier = Modifier.testTag(HomeTestTags.LIBRARY_TAB),
            imageVector = Icons.Outlined.VideoLibrary,
            title = menu_item_library.resolve(context),
            selected = activePresenter is LibraryPresenter,
            onClick = { component.onLibraryClicked() },
        )

        ProfileNavigationItem(
            modifier = Modifier.testTag(HomeTestTags.PROFILE_TAB),
            avatarUrl = avatarUrl,
            title = menu_item_profile.resolve(context),
            selected = activePresenter is ProfilePresenter,
            onClick = { component.onProfileClicked() },
        )
    }
}

@Composable
private fun RowScope.ProfileNavigationItem(
    avatarUrl: String?,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBarItem(
        modifier = modifier,
        icon = {
            AvatarComponent(
                imageUrl = avatarUrl,
                contentDescription = title,
                size = 24.dp,
                placeholderIcon = Icons.Outlined.Person,
                border = BorderStroke(
                    width = 2.dp,
                    color = if (selected) NavigationDefaultColors.navigationSelectedItemColor() else NavigationDefaultColors.navigationContentColor(),
                ),
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
