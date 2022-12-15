package com.thomaskioko.tvmaniac.seasondetails

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

class SeasonDetailsNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<SeasonDetailsViewModel>(
            arguments = listOf(
                navArgument("showId") { type = NavType.IntType },
                navArgument("seasonName") { type = NavType.StringType }
            ),
            route = "${NavigationScreen.SeasonDetailsNavScreen.route}/{showId}/{seasonName}",
            content = {
                SeasonDetailsScreen(
                    viewModel = this,
                    navigateUp = { navController.popBackStack() },
                    initialSeasonName = navController.currentBackStackEntry?.arguments?.getString("seasonName"),
                    onEpisodeClicked = {
                        // TODO:: Navigate to Episode detail module
                    }
                )
            }
        )
    }
}
