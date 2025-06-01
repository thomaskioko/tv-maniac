package com.thomaskioko.tvmaniac.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.thomaskioko.tvmaniac.presentation.home.HomePresenter
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenter
import kotlinx.coroutines.flow.StateFlow

interface RootPresenter {
    interface Factory {
        fun create(
            componentContext: ComponentContext,
            navigator: RootNavigator,
        ): RootPresenter
    }

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
