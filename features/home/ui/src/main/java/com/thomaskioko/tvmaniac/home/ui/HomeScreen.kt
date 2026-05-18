package com.thomaskioko.tvmaniac.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.compose.components.AvatarComponent
import com.thomaskioko.tvmaniac.compose.components.NavigationDefaultColors
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomNavigationItem
import com.thomaskioko.tvmaniac.compose.components.TvManiacNavigationBar
import com.thomaskioko.tvmaniac.core.base.ActivityScope
import com.thomaskioko.tvmaniac.discover.nav.DiscoverRoot
import com.thomaskioko.tvmaniac.i18n.MR.strings.label_tab_watchlist
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_discover
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_profile
import com.thomaskioko.tvmaniac.i18n.MR.strings.menu_item_progress
import com.thomaskioko.tvmaniac.i18n.resolve
import com.thomaskioko.tvmaniac.navigation.BaseRoute
import com.thomaskioko.tvmaniac.navigation.NavRoot
import com.thomaskioko.tvmaniac.navigation.RootChild
import com.thomaskioko.tvmaniac.navigation.stableKey
import com.thomaskioko.tvmaniac.navigation.ui.LocalScreenContents
import com.thomaskioko.tvmaniac.navigation.ui.ScreenContent
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.profile.nav.ProfileRoot
import com.thomaskioko.tvmaniac.progress.nav.ProgressRoot
import com.thomaskioko.tvmaniac.testtags.home.HomeTestTags
import com.thomaskioko.tvmaniac.watchlist.nav.WatchlistRoot
import io.github.thomaskioko.codegen.annotations.ScreenUi

@ScreenUi(presenter = HomePresenter::class, parentScope = ActivityScope::class)
@Composable
public fun HomeScreen(
    presenter: HomePresenter,
    modifier: Modifier = Modifier,
) {
    val screenContents = LocalScreenContents.current
    val hostState by presenter.hostState.collectAsState()
    val saveableStateHolder = rememberSaveableStateHolder()

    val activeRoot = hostState.activeRoot
    val activeStack = hostState.tabStacks[activeRoot]
        ?: error("No back stack registered for $activeRoot.")

    val isAtTabRoot = activeStack.active.configuration is NavRoot

    Column(modifier = modifier) {
        Box(modifier = Modifier.weight(1F).fillMaxSize()) {
            saveableStateHolder.SaveableStateProvider(key = activeRoot.stableKey) {
                TabPane(stack = activeStack, screenContents = screenContents)
            }
        }
        AnimatedVisibility(
            visible = isAtTabRoot,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            BottomNavigationContent(
                component = presenter,
                activeRoot = activeRoot,
                modifier = Modifier.fillMaxWidth().testTag(HomeTestTags.NAVIGATION_BAR),
            )
        }
    }
}

@Composable
private fun TabPane(
    stack: ChildStack<BaseRoute, RootChild>,
    screenContents: Set<ScreenContent>,
) {
    Children(
        modifier = Modifier.fillMaxSize(),
        stack = stack,
    ) { child ->
        val instance = child.instance
        val renderer = screenContents.firstOrNull { it.matches(instance) } ?: return@Children
        renderer.content(instance, Modifier.fillMaxSize())
    }
}

@Composable
internal fun BottomNavigationContent(
    component: HomePresenter,
    activeRoot: NavRoot,
    modifier: Modifier = Modifier,
) {
    val avatarUrl by component.profileAvatarUrl.collectAsState()
    val context = LocalContext.current

    TvManiacNavigationBar(
        modifier = modifier,
    ) {
        TvManiacBottomNavigationItem(
            modifier = Modifier.testTag(HomeTestTags.DISCOVER_TAB),
            imageVector = Icons.Outlined.Movie,
            title = menu_item_discover.resolve(context),
            selected = activeRoot is DiscoverRoot,
            onClick = { component.onDiscoverClicked() },
        )

        TvManiacBottomNavigationItem(
            modifier = Modifier.testTag(HomeTestTags.PROGRESS_TAB),
            imageVector = Icons.Outlined.PlayCircleOutline,
            title = menu_item_progress.resolve(context),
            selected = activeRoot is ProgressRoot,
            onClick = { component.onProgressClicked() },
        )

        TvManiacBottomNavigationItem(
            modifier = Modifier.testTag(HomeTestTags.WATCHLIST_TAB),
            imageVector = Icons.Outlined.Bookmarks,
            title = label_tab_watchlist.resolve(context),
            selected = activeRoot is WatchlistRoot,
            onClick = { component.onWatchlistClicked() },
        )

        ProfileNavigationItem(
            modifier = Modifier.testTag(HomeTestTags.PROFILE_TAB),
            avatarUrl = avatarUrl,
            title = menu_item_profile.resolve(context),
            selected = activeRoot is ProfileRoot,
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
