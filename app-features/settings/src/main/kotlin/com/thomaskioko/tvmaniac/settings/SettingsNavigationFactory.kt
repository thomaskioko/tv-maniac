package com.thomaskioko.tvmaniac.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

class SettingsNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<SettingsViewModel>(
            route = NavigationScreen.SettingsScreen.route,
            content = {
                SettingsScreen(
                    viewModel = this,
                    navController = navController
                )
            }
        )
    }
}
