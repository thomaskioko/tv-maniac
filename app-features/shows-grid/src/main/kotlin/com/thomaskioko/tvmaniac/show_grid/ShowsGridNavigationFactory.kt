package com.thomaskioko.tvmaniac.show_grid

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

class ShowsGridNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<ShowGridViewModel>(
            arguments = listOf(
                navArgument("showType") { type = NavType.IntType },
            ),
            route = "${NavigationScreen.ShowGridNavScreen.route}/{showType}",
            content = {
                ShowsGridScreen(
                    viewModel = this,
                    navigateUp = { navController.popBackStack() },
                    openShowDetails = { tvShowId ->
                        navController.navigate("${NavigationScreen.ShowDetailsNavScreen.route}/$tvShowId")
                    },
                )
            }
        )
    }
}
