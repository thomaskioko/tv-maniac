package com.thomaskioko.tvmaniac.search

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.thomaskioko.tvmaniac.navigation.ComposeNavigationFactory
import com.thomaskioko.tvmaniac.navigation.NavigationScreen
import com.thomaskioko.tvmaniac.navigation.viewModelComposable
import javax.inject.Inject

class SearchNavigationFactory @Inject constructor() : ComposeNavigationFactory {

    override fun create(builder: NavGraphBuilder, navController: NavHostController) {
        builder.viewModelComposable<SearchViewModel>(
            route = NavigationScreen.SearchNavScreen.route,
            content = {
                SearchScreen(
                    viewModel = this,
                    navController = navController
                )
            }
        )
    }
}