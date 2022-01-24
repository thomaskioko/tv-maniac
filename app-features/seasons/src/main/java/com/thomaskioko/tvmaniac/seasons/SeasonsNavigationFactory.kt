package com.thomaskioko.tvmaniac.seasons

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

class SeasonsNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<SeasonsViewModel>(
            arguments = listOf(
                navArgument("showId") { type = NavType.LongType },
                navArgument("seasonNumber") { type = NavType.IntType }
            ),
            route = "${NavigationScreen.SeasonsNavScreen.route}/{showId}/{seasonNumber}",
            content = {
                SeasonsScreen(
                    viewModel = this,
                    navigateUp = { navController.popBackStack() },
                )
            }
        )
    }
}
