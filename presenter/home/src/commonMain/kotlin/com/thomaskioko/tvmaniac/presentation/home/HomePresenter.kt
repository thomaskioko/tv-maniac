package com.thomaskioko.tvmaniac.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.search.SearchShowsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenter
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

    class Library(val presenter: LibraryPresenter) : Child

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
