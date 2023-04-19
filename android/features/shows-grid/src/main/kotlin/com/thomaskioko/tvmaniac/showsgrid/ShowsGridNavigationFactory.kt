package com.thomaskioko.tvmaniac.showsgrid

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.extensions.screenComposable
import me.tatarka.inject.annotations.Inject

@Inject
class ShowsGridNavigationFactory(
    private val showsGrid: ShowsGrid,
) : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.screenComposable(
            arguments = listOf(
                navArgument("showType") { type = NavType.LongType },
            ),
            route = "${NavigationScreen.ShowGridNavScreen.route}/{showType}",
            content = {
                showsGrid(
                    onBackClicked = { navController.popBackStack() },
                    openShowDetails = { tvShowId ->
                        navController.navigate("${NavigationScreen.ShowDetailsNavScreen.route}/$tvShowId")
                    },
                )
            },
        )
    }
}
