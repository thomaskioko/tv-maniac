package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.presentation.home.HomeComponent
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenter
import kotlinx.coroutines.flow.StateFlow

interface RootComponent {
  val stack: StateFlow<ChildStack<*, Child>>
  val themeState: StateFlow<ThemeState>

  fun bringToFront(config: Config)

  fun onBackClicked()

  fun onBackClicked(toIndex: Int)

  sealed interface Child {
    class Home(val component: HomeComponent) : Child

    class ShowDetails(val presenter: ShowDetailsPresenter) : Child

    class SeasonDetails(val presenter: SeasonDetailsPresenter) : Child

    class MoreShows(val presenter: MoreShowsPresenter) : Child

    class Trailers(val presenter: TrailersPresenter) : Child
  }
}
