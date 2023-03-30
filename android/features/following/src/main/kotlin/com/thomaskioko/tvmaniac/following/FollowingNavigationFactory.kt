package com.thomaskioko.tvmaniac.following

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

class FollowingNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<FollowingViewModel>(
            route = NavigationScreen.WatchlistNavScreen.route,
            content = {
                FollowingRoute(
                    viewModel = this,
                    onShowClicked = { tvShowId ->
                        navController.navigate(
                            "${NavigationScreen.ShowDetailsNavScreen.route}/$tvShowId"
                        )
                    }
                )
            }
        )
    }
}
