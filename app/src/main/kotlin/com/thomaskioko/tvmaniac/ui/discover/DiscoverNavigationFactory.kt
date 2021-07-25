package com.thomaskioko.tvmaniac.ui.discover

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

internal class DiscoverNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<DiscoverViewModel>(
            route = NavigationScreen.DiscoverNavScreen.route,
            content = {
                DiscoverScreen(
                    openShowDetails = { tvShowId ->
                        navController.navigate("${NavigationScreen.ShowDetailsNavScreen.route}/$tvShowId")
                    },
                    viewModel = this,
                )
            }
        )
    }
}