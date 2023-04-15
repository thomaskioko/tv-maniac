package com.thomaskioko.tvmaniac.seasondetails

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.extensions.screenComposable
import me.tatarka.inject.annotations.Inject

@Inject
class SeasonDetailsNavigationFactory(
    private val seasonDetails: SeasonDetails
) : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.screenComposable(
            arguments = listOf(
                navArgument("showId") { type = NavType.LongType },
                navArgument("seasonName") { type = NavType.StringType }
            ),
            route = "${NavigationScreen.SeasonDetailsNavScreen.route}/{showId}/{seasonName}",
            content = {
                seasonDetails(
                    initialSeasonName = navController.currentBackStackEntry?.arguments?.getString("seasonName"),
                    onBackClicked = { navController.popBackStack() },
                    onEpisodeClicked = {
                        // TODO:: Navigate to Episode detail module
                    }
                )
            }
        )
    }
}
