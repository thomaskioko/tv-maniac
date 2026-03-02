package com.thomaskioko.tvmaniac.home.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.thomaskioko.tvmaniac.compose.components.CircularCard
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomNavigationItem
import com.thomaskioko.tvmaniac.compose.components.TvManiacNavigationBar
import com.thomaskioko.tvmaniac.discover.ui.DiscoverScreen
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_discover_up_next
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_discover
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_library
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_profile
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.Child.Discover
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.Child.Library
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.Child.Profile
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter.Child.UpNext
import com.thomaskioko.tvmaniac.profile.ui.ProfileScreen
import com.thomaskioko.tvmaniac.ui.library.LibraryScreen
import com.thomaskioko.tvmaniac.ui.upnext.UpNextScreen

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
        when (val screen = child.instance) {
            is Discover -> {
                DiscoverScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            }
            is UpNext -> {
                UpNextScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            }
            is Library -> {
                LibraryScreen(
                    presenter = screen.presenter,
                    modifier = fillMaxSizeModifier,
                )
            }
            is Profile -> {
                ProfileScreen(
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
    val avatarUrl by component.profileAvatarUrl.collectAsState()
    val context = LocalContext.current

    TvManiacNavigationBar(
        modifier = modifier,
    ) {
        TvManiacBottomNavigationItem(
            imageVector = Icons.Outlined.Movie,
            title = menu_item_discover.resolve(context),
            selected = activeComponent is Discover,
            onClick = { component.onDiscoverClicked() },
        )

        TvManiacBottomNavigationItem(
            imageVector = Icons.Outlined.PlayCircleOutline,
            title = label_discover_up_next.resolve(context),
            selected = activeComponent is UpNext,
            onClick = { component.onUpNextClicked() },
        )

        TvManiacBottomNavigationItem(
            imageVector = Icons.Outlined.VideoLibrary,
            title = menu_item_library.resolve(context),
            selected = activeComponent is Library,
            onClick = { component.onLibraryClicked() },
        )

        ProfileNavigationItem(
            avatarUrl = avatarUrl,
            title = menu_item_profile.resolve(context),
            selected = activeComponent is Profile,
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
) {
    NavigationBarItem(
        icon = {
            if (avatarUrl != null) {
                CircularCard(
                    imageUrl = avatarUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(0.dp),
                )
            } else {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = title,
                )
            }
        },
        label = { Text(title) },
        selected = selected,
        alwaysShowLabel = true,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.secondary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedTextColor = MaterialTheme.colorScheme.secondary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            indicatorColor = Color.Transparent,
        ),
        onClick = onClick,
    )
}
