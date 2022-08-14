package com.thomaskioko.tvmaniac.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet

inline fun <reified VM> NavGraphBuilder.viewModelComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable VM.(NavBackStackEntry) -> Unit
) where VM : ViewModel {

    composable(route, arguments, deepLinks) { navBackStackEntry ->
        content(hiltViewModel(), navBackStackEntry)
    }
}


@OptIn(ExperimentalMaterialNavigationApi::class)
inline fun <reified VM> NavGraphBuilder.viewModelBottomSheetComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    crossinline content: @Composable VM.(NavBackStackEntry) -> Unit
) where VM : ViewModel {

    bottomSheet(route, arguments, deepLinks) { navBackStackEntry ->
            content(hiltViewModel(), navBackStackEntry)
    }
}

