package com.thomaskioko.tvmaniac.navigation

import com.thomaskioko.tvmaniac.presentation.discover.DiscoverShowsPresenter
import com.thomaskioko.tvmaniac.presentation.moreshows.MoreShowsPresenter
import com.thomaskioko.tvmaniac.presentation.search.SearchPresenter
import com.thomaskioko.tvmaniac.presentation.seasondetails.SeasonDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.settings.SettingsPresenter
import com.thomaskioko.tvmaniac.presentation.showdetails.ShowDetailsPresenter
import com.thomaskioko.tvmaniac.presentation.trailers.TrailersPresenter
import com.thomaskioko.tvmaniac.presentation.watchlist.LibraryPresenter

internal sealed interface Screen {
    class Discover(val presenter: DiscoverShowsPresenter) : Screen
    class Library(val presenter: LibraryPresenter) : Screen
    class MoreShows(val presenter: MoreShowsPresenter) : Screen
    class Search(val presenter: SearchPresenter) : Screen
    class SeasonDetails(val presenter: SeasonDetailsPresenter) : Screen
    class Settings(val presenter: SettingsPresenter) : Screen
    class ShowDetails(val presenter: ShowDetailsPresenter) : Screen
    class Trailers(val presenter: TrailersPresenter) : Screen
}
