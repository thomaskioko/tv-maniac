package com.thomaskioko.tvmaniac.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.thomaskioko.tvmaniac.discover.DiscoverScreen
import com.thomaskioko.tvmaniac.resources.R
import com.thomaskioko.tvmaniac.search.SearchScreen
import com.thomaskioko.tvmaniac.settings.SettingsScreen
import com.thomaskioko.tvmaniac.showsgrid.LibraryScreen
import kotlin.reflect.KClass

interface BottomBarItem {
    val stringResourceId: Int
    val imageVector: ImageVector
    val screenKlass: KClass<*>
    val screen: () -> Screen

    fun isSelected(navigator: Navigator) = navigator.items.first()::class == screenKlass
}

enum class BottomBarItems(
    override val stringResourceId: Int,
    override val imageVector: ImageVector,
    override val screenKlass: KClass<*>,
    override val screen: () -> Screen,
) : BottomBarItem {
    DISCOVER(
        stringResourceId = R.string.menu_item_discover,
        imageVector = Icons.Outlined.Movie,
        screenKlass = DiscoverScreen::class,
        screen = { DiscoverScreen },
    ),
    SEARCH(
        stringResourceId = R.string.menu_item_search,
        imageVector = Icons.Outlined.Search,
        screenKlass = SearchScreen::class,
        screen = { SearchScreen },
    ),
    LIBRARY(
        stringResourceId = R.string.menu_item_follow,
        imageVector = Icons.Outlined.Star,
        screenKlass = LibraryScreen::class,
        screen = { LibraryScreen },
    ),
    SETTINGS(
        stringResourceId = R.string.menu_item_settings,
        imageVector = Icons.Outlined.Settings,
        screenKlass = SettingsScreen::class,
        screen = { SettingsScreen },
    ),
}
