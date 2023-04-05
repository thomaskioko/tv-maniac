package com.thomaskioko.tvmaniac.videoplayer

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelBottomSheetComposable
import javax.inject.Inject

class TrailerNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelBottomSheetComposable<TrailersViewModel>(
            arguments = listOf(
                navArgument("showId") { type = NavType.LongType },
                navArgument("videoKey") { type = NavType.StringType },
            ),
            route = "${NavigationScreen.TrailersNavScreen.route}/{showId}/{videoKey}",
            content = {
                TrailersRoute(
                    viewModel = this,
                )
            }
        )
    }
}
