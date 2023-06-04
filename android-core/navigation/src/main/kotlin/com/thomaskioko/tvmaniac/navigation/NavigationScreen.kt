package com.thomaskioko.tvmaniac.navigation

sealed class NavigationScreen(val route: String) {
    object DiscoverNavScreen : NavigationScreen("discover")
    object SearchNavScreen : NavigationScreen("search")
    object WatchlistNavScreen : NavigationScreen("watchlist")
    object ShowDetailsNavScreen : NavigationScreen("details")
    object SettingsNavScreen : NavigationScreen("settings")
    object ShowGridNavScreen : NavigationScreen("show_grid")
    object SeasonDetailsNavScreen : NavigationScreen("seasons")
    object TrailersNavScreen : NavigationScreen("trailers")
    object ProfileNavScreen : NavigationScreen("profile")
}
