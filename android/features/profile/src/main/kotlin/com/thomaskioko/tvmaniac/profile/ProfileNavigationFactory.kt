package com.thomaskioko.tvmaniac.profile

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.extensions.screenComposable
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileNavigationFactory(
    private val profile: Profile
) : ComposeNavigationFactory {
    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.screenComposable(
            route = NavigationScreen.ProfileNavScreen.route,
            content = {
                profile(
                    settingsClicked = {
                        navController.navigate(NavigationScreen.SettingsScreen.route)
                    }
                )
            }
        )
    }
}
