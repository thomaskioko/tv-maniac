package com.thomaskioko.tvmaniac.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.addNavigation
import com.thomaskioko.tvmaniac.resources.R

@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun HomeScreen(
    factorySet: Set<ComposeNavigationFactory>,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val route = currentRoute(navController)
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    Scaffold(
        modifier = modifier
            .navigationBarsPadding(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            val currentSelectedItem by navController.currentScreenAsState()
            val showBottomBar = route in listOf(
                NavigationScreen.DiscoverNavScreen.route,
                NavigationScreen.SearchNavScreen.route,
                NavigationScreen.WatchlistNavScreen.route,
                NavigationScreen.ProfileNavScreen.route,
                NavigationScreen.SettingsNavScreen.route,
            )
            AnimatedVisibility(visible = showBottomBar) {
                TvManiacBottomNavigation(
                    onNavigationSelected = { selected ->
                        navController.navigate(selected.route) {
                            launchSingleTop = true
                            restoreState = true

                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    },
                    currentSelectedItem = currentSelectedItem,
                )
            }
        },
    ) { contentPadding ->
        ModalBottomSheetLayout(bottomSheetNavigator) {
            NavHost(
                navController = navController,
                startDestination = NavigationScreen.DiscoverNavScreen.route,
                modifier = Modifier.padding(contentPadding),
            ) {
                factorySet.addNavigation(this, navController)
            }
        }
    }
}

@Composable
private fun TvManiacBottomNavigation(
    currentSelectedItem: NavigationScreen,
    onNavigationSelected: (NavigationScreen) -> Unit,
    modifier: Modifier = Modifier,
) {
    BottomNavigation(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.surface,
    ) {
        TvManiacBottomNavigationItem(
            screen = NavigationScreen.DiscoverNavScreen,
            imageVector = Icons.Outlined.Movie,
            title = stringResource(id = R.string.menu_item_discover),
            selected = currentSelectedItem == NavigationScreen.DiscoverNavScreen,
            onNavigationSelected = onNavigationSelected,
        )

        TvManiacBottomNavigationItem(
            screen = NavigationScreen.SearchNavScreen,
            imageVector = Icons.Outlined.Search,
            title = stringResource(id = R.string.menu_item_search),
            selected = currentSelectedItem == NavigationScreen.SearchNavScreen,
            onNavigationSelected = onNavigationSelected,
        )

        TvManiacBottomNavigationItem(
            screen = NavigationScreen.WatchlistNavScreen,
            imageVector = Icons.Outlined.Star,
            title = stringResource(id = R.string.menu_item_follow),
            selected = currentSelectedItem == NavigationScreen.WatchlistNavScreen,
            onNavigationSelected = onNavigationSelected,
        )

        TvManiacBottomNavigationItem(
            screen = NavigationScreen.SettingsNavScreen,
            imageVector = Icons.Filled.Settings,
            title = stringResource(id = R.string.menu_item_settings),
            selected = currentSelectedItem == NavigationScreen.SettingsNavScreen,
            onNavigationSelected = onNavigationSelected,
        )
    }
}

@Composable
fun RowScope.TvManiacBottomNavigationItem(
    screen: NavigationScreen,
    imageVector: ImageVector,
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onNavigationSelected: (NavigationScreen) -> Unit,
) {
    BottomNavigationItem(
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
        selectedContentColor = MaterialTheme.colorScheme.secondary,
        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
        onClick = { onNavigationSelected(screen) },
    )
}

@Composable
private fun currentRoute(navController: NavHostController): String {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route ?: NavigationScreen.DiscoverNavScreen.route
}

/**
 * Adds an [NavController.OnDestinationChangedListener] to this [NavController] and updates the
 * returned [State] which is updated as the destination changes.
 */
@Stable
@Composable
private fun NavController.currentScreenAsState(): State<NavigationScreen> {
    val selectedItem =
        remember { mutableStateOf<NavigationScreen>(NavigationScreen.DiscoverNavScreen) }

    DisposableEffect(this) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hierarchy.any { it.route == NavigationScreen.DiscoverNavScreen.route } -> {
                    selectedItem.value = NavigationScreen.DiscoverNavScreen
                }

                destination.hierarchy.any { it.route == NavigationScreen.SearchNavScreen.route } -> {
                    selectedItem.value = NavigationScreen.SearchNavScreen
                }

                destination.hierarchy.any { it.route == NavigationScreen.WatchlistNavScreen.route } -> {
                    selectedItem.value = NavigationScreen.WatchlistNavScreen
                }

                destination.hierarchy.any { it.route == NavigationScreen.ProfileNavScreen.route } -> {
                    selectedItem.value = NavigationScreen.ProfileNavScreen
                }
            }
        }
        addOnDestinationChangedListener(listener)

        onDispose {
            removeOnDestinationChangedListener(listener)
        }
    }

    return selectedItem
}
