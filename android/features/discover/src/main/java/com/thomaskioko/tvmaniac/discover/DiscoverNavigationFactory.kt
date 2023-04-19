package com.thomaskioko.tvmaniac.discover

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.extensions.screenComposable
import me.tatarka.inject.annotations.Inject

@Inject
class DiscoverNavigationFactory(
    private val discover: Discover,
) : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.screenComposable(
            route = NavigationScreen.DiscoverNavScreen.route,
            content = {
                discover(
                    onShowClicked = { tvShowId ->
                        navController.navigate(
                            "${NavigationScreen.ShowDetailsNavScreen.route}/$tvShowId",
                        )
                    },
                    onMoreClicked = { showType ->
                        navController.navigate(
                            "${NavigationScreen.ShowGridNavScreen.route}/$showType",
                        )
                    },
                )
            },
        )
    }
}
