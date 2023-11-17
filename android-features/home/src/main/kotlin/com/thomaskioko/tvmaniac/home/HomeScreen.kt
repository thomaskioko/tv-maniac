package com.thomaskioko.tvmaniac.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
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
import com.thomaskioko.tvmaniac.common.localization.MR
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomNavigationItem
import com.thomaskioko.tvmaniac.compose.components.TvManiacNavigationBar
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.addNavigation

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
        modifier = modifier,
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
    TvManiacNavigationBar(
        modifier = modifier,
    ) {
        TvManiacBottomNavigationItem(
            imageVector = Icons.Outlined.Movie,
            title = stringResource(id = MR.strings.tab_item_discover.resourceId),
            selected = currentSelectedItem == NavigationScreen.DiscoverNavScreen,
            onClick = { onNavigationSelected(NavigationScreen.DiscoverNavScreen) },
        )

        TvManiacBottomNavigationItem(
            imageVector = Icons.Outlined.Search,
            title = stringResource(id = MR.strings.tab_item_search.resourceId),
            selected = currentSelectedItem == NavigationScreen.SearchNavScreen,
            onClick = { onNavigationSelected(NavigationScreen.SearchNavScreen) },
        )

        TvManiacBottomNavigationItem(
            imageVector = Icons.Outlined.Star,
            title = stringResource(id = MR.strings.tab_item_follow.resourceId),
            selected = currentSelectedItem == NavigationScreen.WatchlistNavScreen,
            onClick = { onNavigationSelected(NavigationScreen.WatchlistNavScreen) },
        )

        TvManiacBottomNavigationItem(
            imageVector = Icons.Filled.Settings,
            title = stringResource(id = MR.strings.tab_item_settings.resourceId),
            selected = currentSelectedItem == NavigationScreen.SettingsNavScreen,
            onClick = { onNavigationSelected(NavigationScreen.SettingsNavScreen) },
        )
    }
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

                destination.hierarchy.any { it.route == NavigationScreen.SettingsNavScreen.route } -> {
                    selectedItem.value = NavigationScreen.SettingsNavScreen
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
