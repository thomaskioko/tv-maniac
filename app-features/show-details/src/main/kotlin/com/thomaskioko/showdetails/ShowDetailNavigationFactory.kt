package com.thomaskioko.showdetails

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

class ShowDetailNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<ShowDetailsViewModel>(
            arguments = listOf(
                navArgument("tvShowId") { type = NavType.IntType }
            ),
            route = "${NavigationScreen.ShowDetailsNavScreen.route}/{tvShowId}",
            content = {
                ShowDetailScreen(
                    viewModel = this,
                    navigateUp = { navController.popBackStack() },
                )
            }
        )
    }
}