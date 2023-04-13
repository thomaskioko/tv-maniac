package com.thomaskioko.showdetails

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.extensions.screenComposable
import me.tatarka.inject.annotations.Inject

@Inject
class ShowDetailNavigationFactory(
    private val showDetail: ShowDetail
) : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.screenComposable(
            arguments = listOf(
                navArgument("tvShowId") { type = NavType.LongType }
            ),
            route = "${NavigationScreen.ShowDetailsNavScreen.route}/{tvShowId}",
            content = {
                showDetail(
                    onBackClicked = { navController.popBackStack() },
                    onShowClicked = { showId ->
                        navController.navigate("${NavigationScreen.ShowDetailsNavScreen.route}/$showId")
                    },
                    onSeasonClicked = { showId, seasonName ->
                        navController.navigate("${NavigationScreen.SeasonDetailsNavScreen.route}/$showId/$seasonName")
                    },
                    onWatchTrailerClicked = { showId, videoKey ->
                        navController.navigate("${NavigationScreen.TrailersNavScreen.route}/$showId/$videoKey")
                    }
                )
            }
        )
    }
}
