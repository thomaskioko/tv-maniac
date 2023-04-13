package com.thomaskioko.tvmaniac.settings

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.extensions.screenComposable
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsNavigationFactory(
    private val settings: Settings
) : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.screenComposable(
            route = NavigationScreen.SettingsScreen.route,
            content = {
                settings(
                    onBackClicked = { navController.popBackStack() },
                )
            }
        )
    }
}
