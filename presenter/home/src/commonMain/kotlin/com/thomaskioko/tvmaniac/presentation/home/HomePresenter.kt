package com.thomaskioko.tvmaniac.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.WatchlistPresenter
import com.thomaskioko.tvmaniac.search.presentation.SearchShowsPresenter
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

interface HomePresenter {
    interface Factory {
        fun create(
            componentContext: ComponentContext,
            onShowClicked: (id: Long) -> Unit,
            onMoreShowClicked: (id: Long) -> Unit,
            onShowGenreClicked: (id: Long) -> Unit,
        ): HomePresenter
    }

    val homeChildStack: StateFlow<ChildStack<*, Child>>

    fun onDiscoverClicked()
    fun onLibraryClicked()
    fun onSearchClicked()
    fun onSettingsClicked()
    fun onTabClicked(config: HomeConfig)

    sealed interface Child {
        class Discover(val presenter: DiscoverShowsPresenter) : Child

        class Watchlist(val presenter: WatchlistPresenter) : Child

        class Search(val presenter: SearchShowsPresenter) : Child

        class Settings(val presenter: SettingsPresenter) : Child
    }

    @Serializable
    sealed interface HomeConfig {
        @Serializable
        data object Discover : HomeConfig

        @Serializable
        data object Library : HomeConfig

        @Serializable
        data object Search : HomeConfig

        @Serializable
        data object Settings : HomeConfig
    }
}
