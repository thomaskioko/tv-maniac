package com.thomaskioko.tvmaniac.ui.welcome

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

internal class WelcomeNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<WelcomeViewModel>(
            route = NavigationScreen.WelcomeNavScreen.route,
            content = {
                WelcomeScreen(
                    viewModel = this,
                    navController = navController
                )
            }
        )
    }
}