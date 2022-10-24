package com.thomaskioko.tvmaniac.videoplayer

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelBottomSheetComposable
import javax.inject.Inject

class VideoPlayerNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelBottomSheetComposable<VideoPlayerViewModel>(
            arguments = listOf(
                navArgument("showId") { type = NavType.IntType },
                navArgument("videoKey") { type = NavType.StringType },
            ),
            route = "${NavigationScreen.VideoPlayerNavScreen.route}/{showId}/{videoKey}",
            content = {
                VideoPlayerScreen(
                    viewModel = this,
                )
            }
        )
    }
}
