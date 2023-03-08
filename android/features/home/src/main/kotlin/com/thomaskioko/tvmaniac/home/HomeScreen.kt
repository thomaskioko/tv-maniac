package com.thomaskioko.tvmaniac.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
    composeNavigationFactories: Set<ComposeNavigationFactory>
) {

    val navController = rememberNavController()
    val route = currentRoute(navController)
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator

    Scaffold(
        modifier = Modifier
            .navigationBarsPadding(),
        bottomBar = {
            val currentSelectedItem by navController.currentScreenAsState()
            val showBottomBar = route in listOf(
                NavigationScreen.DiscoverNavScreen.route,
                NavigationScreen.SearchNavScreen.route,
                NavigationScreen.WatchlistNavScreen.route,
                NavigationScreen.ProfileNavScreen.route,
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
                    currentSelectedItem = currentSelectedItem
                )
            }
        }
    ) { contentPadding ->
        ModalBottomSheetLayout(bottomSheetNavigator) {
            NavHost(
                navController = navController,
                startDestination = NavigationScreen.DiscoverNavScreen.route,
                modifier = Modifier.padding(contentPadding)
            ) {
                composeNavigationFactories.addNavigation(this, navController)
            }
        }
    }
}

@Composable
private fun TvManiacBottomNavigation(
    currentSelectedItem: NavigationScreen,
    onNavigationSelected: (NavigationScreen) -> Unit
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary
    ) {

        TvManiacBottomNavigationItem(
            screen = NavigationScreen.DiscoverNavScreen,
            imageVector = Icons.Outlined.Movie,
            title = stringResource(id = R.string.menu_item_discover),
            selected = currentSelectedItem == NavigationScreen.DiscoverNavScreen,
            onNavigationSelected = onNavigationSelected
        )

        TvManiacBottomNavigationItem(
            screen = NavigationScreen.SearchNavScreen,
            imageVector = Icons.Outlined.Search,
            title = stringResource(id = R.string.menu_item_search),
            selected = currentSelectedItem == NavigationScreen.SearchNavScreen,
            onNavigationSelected = onNavigationSelected
        )

        TvManiacBottomNavigationItem(
            screen = NavigationScreen.WatchlistNavScreen,
            imageVector = Icons.Outlined.Star,
            title = stringResource(id = R.string.menu_item_follow),
            selected = currentSelectedItem == NavigationScreen.WatchlistNavScreen,
            onNavigationSelected = onNavigationSelected
        )

        TvManiacBottomNavigationItem(
            screen = NavigationScreen.ProfileNavScreen,
            imageVector = Icons.Filled.AccountCircle,
            title = stringResource(id = R.string.menu_item_profile),
            selected = currentSelectedItem == NavigationScreen.ProfileNavScreen,
            onNavigationSelected = onNavigationSelected
        )
    }
}

@Composable
fun RowScope.TvManiacBottomNavigationItem(
    screen: NavigationScreen,
    imageVector: ImageVector,
    title: String,
    selected: Boolean,
    onNavigationSelected: (NavigationScreen) -> Unit
) {
    BottomNavigationItem(
        icon = {
            Icon(
                imageVector = imageVector,
                contentDescription = title
            )
        },
        label = { Text(title) },
        selected = selected,
        alwaysShowLabel = true,
        selectedContentColor = MaterialTheme.colors.secondary,
        unselectedContentColor = MaterialTheme.colors.onSurface,
        onClick = { onNavigationSelected(screen) }
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
