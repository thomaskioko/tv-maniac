package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.moreshows.presentation.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presenter.home.HomePresenter
import com.thomaskioko.tvmaniac.presenter.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presenter.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.seasondetails.presenter.SeasonDetailsPresenter
import kotlinx.coroutines.flow.StateFlow

interface RootPresenter {
    val childStack: StateFlow<ChildStack<*, Child>>

    val themeState: StateFlow<ThemeState>

    sealed interface Child {
        class Home(val presenter: HomePresenter) : Child

        class ShowDetails(val presenter: ShowDetailsPresenter) : Child

        class SeasonDetails(val presenter: SeasonDetailsPresenter) : Child

        class MoreShows(val presenter: MoreShowsPresenter) : Child

        class Trailers(val presenter: TrailersPresenter) : Child

        data object GenreShows : Child
    }
}
