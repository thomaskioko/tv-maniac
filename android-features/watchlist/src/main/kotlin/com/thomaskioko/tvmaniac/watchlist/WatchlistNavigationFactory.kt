package com.thomaskioko.tvmaniac.watchlist

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.extensions.screenComposable
import me.tatarka.inject.annotations.Inject

@Inject
class WatchlistNavigationFactory(
    private val watchlist: WatchList,
) : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.screenComposable(
            route = NavigationScreen.WatchlistNavScreen.route,
            content = {
                watchlist(
                    onShowClicked = { tvShowId ->
                        navController.navigate(
                            "${NavigationScreen.ShowDetailsNavScreen.route}/$tvShowId",
                        )
                    },
                )
            },
        )
    }
}
