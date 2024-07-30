package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.presentation.home.HomeComponent
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsComponent
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsComponent
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsComponent
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersComponent
import kotlinx.coroutines.flow.StateFlow

interface RootComponent {
  val stack: StateFlow<ChildStack<*, Child>>
  val themeState: StateFlow<ThemeState>

  fun bringToFront(config: Config)

  fun onBackClicked()

  fun onBackClicked(toIndex: Int)

  sealed interface Child {
    class Home(val component: HomeComponent) : Child

    class ShowDetails(val component: ShowDetailsComponent) : Child

    class SeasonDetails(val component: SeasonDetailsComponent) : Child

    class MoreShows(val component: MoreShowsComponent) : Child

    class Trailers(val component: TrailersComponent) : Child
  }
}
