package com.thomaskioko.tvmaniac.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.BottomNavigation
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thomaskioko.tvmaniac.compose.components.TvManiacBottomNavigationItem
import com.thomaskioko.tvmaniac.compose.theme.TvManiacTheme
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.NavigationScreen.DiscoverNavScreen
import com.thomaskioko.tvmaniac.navigation.NavigationScreen.WelcomeNavScreen
import com.thomaskioko.tvmaniac.navigation.TabScreens
import com.thomaskioko.tvmaniac.navigation.addNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var composeNavigationFactories: @JvmSuppressWildcards Set<ComposeNavigationFactory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TvManiacTheme {

                val bottomNavigationItems = listOf(
                    TabScreens.Discover,
                    TabScreens.Search,
                    TabScreens.Watchlist,
                )

                val navController = rememberNavController()
                val route = currentRoute(navController)

                Scaffold(
                    bottomBar = {
                        when {
                            !route.contains(NavigationScreen.ShowDetailsNavScreen.route) -> {
                                TvManiacBottomNavigation(navController, bottomNavigationItems)
                            }
                        }
                    },
                    content = {
                        NavHost(
                            navController,
                            startDestination = WelcomeNavScreen.route
                        ) {
                            composeNavigationFactories.addNavigation(this, navController)
                        }
                    }
                )

            }
        }
    }

    @Composable
    private fun TvManiacBottomNavigation(
        navController: NavHostController,
        bottomNavigationItems: List<TabScreens>
    ) {
        BottomNavigation(
            backgroundColor = MaterialTheme.colors.primary
        ) {
            val currentRoute = currentRoute(navController)

            bottomNavigationItems.forEach { screen ->
                TvManiacBottomNavigationItem(screen, currentRoute, navController)
            }
        }
    }

    @Composable
    private fun currentRoute(navController: NavHostController): String {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        return navBackStackEntry?.destination?.route ?: DiscoverNavScreen.route
    }
}
