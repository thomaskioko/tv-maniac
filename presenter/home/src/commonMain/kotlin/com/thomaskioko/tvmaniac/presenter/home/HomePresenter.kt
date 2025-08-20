package com.thomaskioko.tvmaniac.presenter.home

import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.discover.presenter.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.search.presenter.SearchShowsPresenter
import com.thomaskioko.tvmaniac.settings.presenter.SettingsPresenter
import com.thomaskioko.tvmaniac.watchlist.presenter.WatchlistPresenter
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

interface HomePresenter {
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
