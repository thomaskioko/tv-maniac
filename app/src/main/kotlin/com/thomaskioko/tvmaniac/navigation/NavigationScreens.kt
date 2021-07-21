package com.thomaskioko.tvmaniac.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.thomaskioko.tvmaniac.R
import com.thomaskioko.tvmaniac.navigation.NavigationScreen.DiscoverNavScreen
import com.thomaskioko.tvmaniac.navigation.NavigationScreen.SearchNavScreen
import com.thomaskioko.tvmaniac.navigation.NavigationScreen.WatchlistNavScreen

sealed class NavigationScreen(val route: String) {
    object DiscoverNavScreen : NavigationScreen("discover")
    object SearchNavScreen : NavigationScreen("search")
    object WatchlistNavScreen : NavigationScreen("watchlist")
    object ShowDetailsNavScreen : NavigationScreen("details")
    object WelcomeNavScreen : NavigationScreen("welcome")
}

sealed class TabScreens(
    val route: String,
    @StringRes val resourceId: Int,
    @DrawableRes val icon: Int
) {
    object Discover : TabScreens(DiscoverNavScreen.route, R.string.menu_item_discover, R.drawable.ic_baseline_discover_24)
    object Search : TabScreens(SearchNavScreen.route, R.string.menu_item_search, R.drawable.ic_baseline_search_24)
    object Watchlist : TabScreens(WatchlistNavScreen.route, R.string.menu_item_watchlist, R.drawable.ic_baseline_watchlist_24)
}
