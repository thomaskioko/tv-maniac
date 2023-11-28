package com.thomaskioko.tvmaniac.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class TvManiacScreens : ScreenProvider {
    data object DiscoverScreen : TvManiacScreens()
    data object SearchScreen : TvManiacScreens()
    data object ProfileScreen : TvManiacScreens()
    data object SettingsScreen : TvManiacScreens()
    data object LibraryScreen : TvManiacScreens()
    data class ShowsGridScreen(val id: Long) : TvManiacScreens()
    data class ShowDetailsScreen(val id: Long) : TvManiacScreens()
    data class SeasonDetails(val id: Long) : TvManiacScreens()
    data class TrailersScreen(val id: Long) : TvManiacScreens()
}
