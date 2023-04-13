package com.thomaskioko.tvmaniac.following

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.extensions.screenComposable
import me.tatarka.inject.annotations.Inject

@Inject
class FollowingNavigationFactory(
    private val following: Following
) : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.screenComposable(
            route = NavigationScreen.WatchlistNavScreen.route,
            content = {
                following(
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
