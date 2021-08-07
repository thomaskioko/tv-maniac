package com.thomaskioko.tvmaniac.watchlist

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

class WatchlistNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<WatchlistViewModel>(
            route = NavigationScreen.WatchlistNavScreen.route,
            content = {
                WatchListScreen(
                    viewModel = this,
                    navController = navController
                )
            }
        )
    }
}