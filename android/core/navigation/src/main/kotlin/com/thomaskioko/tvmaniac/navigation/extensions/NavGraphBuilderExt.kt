package com.thomaskioko.tvmaniac.navigation.extensions

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

inline fun NavGraphBuilder.screenComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(route, arguments, deepLinks) { navBackStackEntry ->
        content(navBackStackEntry)
    }
}

@OptIn(ExperimentalMaterialNavigationApi::class)
inline fun NavGraphBuilder.bottomSheetComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable (NavBackStackEntry) -> Unit,
) {
    bottomSheet(route, arguments, deepLinks) { navBackStackEntry ->
        content(navBackStackEntry)
    }
}
