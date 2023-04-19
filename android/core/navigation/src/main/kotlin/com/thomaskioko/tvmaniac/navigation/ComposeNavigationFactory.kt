package com.thomaskioko.tvmaniac.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

interface ComposeNavigationFactory {
    fun create(builder: NavGraphBuilder, navController: NavHostController)
}

fun Iterable<ComposeNavigationFactory>.addNavigation(
    builder: NavGraphBuilder,
    navController: NavHostController,
) {
    forEach { factory ->
        factory.create(builder, navController)
    }
}
